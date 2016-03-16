#
# base recipe: meta/recipes-devtools/python/python3_3.4.3.bb
# base branch: jethro
# 

require python3.inc

DEPENDS = "python3-native libffi bzip2 db gdbm openssl readline sqlite3 zlib virtual/libintl xz"
PR = "${INC_PR}"

PYTHON_BINABI= "${PYTHON_MAJMIN}m"
# now-avoid-pgen.patch
#   temporarily avoiding build pgen for avoid errors when cross compile
SRC_URI += " \
	file://now-avoid-pgen.patch \
	file://python-config.patch \
	file://020-dont-compile-python-files.patch \
	file://030-fixup-include-dirs.patch \
	file://070-dont-clean-ipkg-install.patch \
	file://080-distutils-dont_adjust_files.patch \
	file://110-enable-zlib.patch \
	file://130-readline-setup.patch \
	file://150-fix-setupterm.patch \
	file://0001-h2py-Fix-issue-13032-where-it-fails-with-UnicodeDeco.patch \
	file://makerace.patch \
	file://03-fix-tkinter-detection.patch \
	file://04-default-is-optimized.patch \ 
	file://avoid_warning_about_tkinter.patch \
	file://cgi_py.patch \
	file://host_include_contamination.patch \
	file://shutil-follow-symlink-fix.patch \
	file://sysroot-include-headers.patch \
	file://unixccompiler.patch \
	file://avoid-ncursesw-include-path.patch \
	file://python3-use-CROSSPYTHONPATH-for-PYTHON_FOR_BUILD.patch \
	file://sysconfig.py-add-_PYTHON_PROJECT_SRC.patch \
	file://setup.py-find-libraries-in-staging-dirs.patch \
"

DEBIAN_PATCH_TYPE = "quilt"
#DEBIAN_PATCH_TYPE = "abnormal"

inherit multilib_header python3native pkgconfig

CONFIGUREOPTS += " --with-system-ffi "

CACHED_CONFIGUREVARS = " \
	ac_cv_have_chflags=no \
	ac_cv_have_lchflags=no \
	ac_cv_have_long_long_format=yes \
	ac_cv_buggy_getaddrinfo=no \
	ac_cv_file__dev_ptmx=yes \
	ac_cv_file__dev_ptc=no \
"

# The 3 lines below are copied from the libffi recipe, ctypes ships its own copy of the libffi sources
#Somehow gcc doesn't set __SOFTFP__ when passing -mfloatabi=softp :(
TARGET_CC_ARCH_append_armv6 = " -D__SOFTFP__"
TARGET_CC_ARCH_append_armv7a = " -D__SOFTFP__"
TARGET_CC_ARCH += "-DNDEBUG -fno-inline"
EXTRA_OEMAKE += "CROSS_COMPILE=yes"
EXTRA_OECONF += "CROSSPYTHONPATH=${STAGING_LIBDIR_NATIVE}/python${PYTHON_MAJMIN}/lib-dynload/ --without-ensurepip"

export CROSS_COMPILE = "${TARGET_PREFIX}"
export _PYTHON_PROJECT_BASE = "${B}"
export _PYTHON_PROJECT_SRC = "${S}"
export CCSHARED = "-fPIC"

# Fix ctypes cross compilation
export CROSSPYTHONPATH = "${B}/build/lib.linux-${TARGET_ARCH}-${PYTHON_MAJMIN}:${S}/Lib:${S}/Lib/plat-linux"

# No ctypes option for python 3
PYTHONLSBOPTS = ""
do_configure_append() {
	rm -f ${S}/Makefile.orig
	autoreconf -Wcross --verbose --install --force --exclude=autopoint ${S}/Modules/_ctypes/libffi
}

do_compile() {
	# regenerate platform specific files, because they depend on system headers
	cd ${S}/Lib/plat-linux*
	include=${STAGING_INCDIR} ${STAGING_BINDIR_NATIVE}/python3-native/python3 \
		${S}/Tools/scripts/h2py.py -i '(u_long)' \
		${STAGING_INCDIR}/dlfcn.h \
		${STAGING_INCDIR}/linux/cdrom.h \
		${STAGING_INCDIR}/netinet/in.h \
		${STAGING_INCDIR}/sys/types.h
	sed -e 's,${STAGING_DIR_HOST},,g' -i *.py
	cd -

	# remove any bogus LD_LIBRARY_PATH
	sed -i -e s,RUNSHARED=.*,RUNSHARED=, Makefile

	if [ ! -f Makefile.orig ]; then
		install -m 0644 Makefile Makefile.orig
	fi
	sed -i -e 's,^CONFIGURE_LDFLAGS=.*,CONFIGURE_LDFLAGS=-L. -L${STAGING_LIBDIR},g' \
		-e 's,libdir=${libdir},libdir=${STAGING_LIBDIR},g' \
		-e 's,libexecdir=${libexecdir},libexecdir=${STAGING_DIR_HOST}${libexecdir},g' \
		-e 's,^LIBDIR=.*,LIBDIR=${STAGING_LIBDIR},g' \
		-e 's,includedir=${includedir},includedir=${STAGING_INCDIR},g' \
		-e 's,^INCLUDEDIR=.*,INCLUDE=${STAGING_INCDIR},g' \
		-e 's,^CONFINCLUDEDIR=.*,CONFINCLUDE=${STAGING_INCDIR},g' \
		Makefile
	# save copy of it now, because if we do it in do_install and 
	# then call do_install twice we get Makefile.orig == Makefile.sysroot
	install -m 0644 Makefile Makefile.sysroot

	oe_runmake HOSTPGEN=${STAGING_BINDIR_NATIVE}/python3-native/pgen \
		HOSTPYTHON=${STAGING_BINDIR_NATIVE}/python3-native/python3 \
		STAGING_LIBDIR=${STAGING_LIBDIR} \
		STAGING_BASELIBDIR=${STAGING_BASELIBDIR} \
		STAGING_INCDIR=${STAGING_INCDIR} \
		BUILD_SYS=${BUILD_SYS} HOST_SYS=${HOST_SYS} \
		LIB=${baselib} \
		ARCH=${TARGET_ARCH} \
		OPT="${CFLAGS}" libpython3.so 

	oe_runmake HOSTPGEN=${STAGING_BINDIR_NATIVE}/python3-native/pgen \
		HOSTPYTHON=${STAGING_BINDIR_NATIVE}/python3-native/python3 \
		STAGING_LIBDIR=${STAGING_LIBDIR} \
		STAGING_INCDIR=${STAGING_INCDIR} \
		STAGING_BASELIBDIR=${STAGING_BASELIBDIR} \
		BUILD_SYS=${BUILD_SYS} HOST_SYS=${HOST_SYS} \
		LIB=${baselib} \
		ARCH=${TARGET_ARCH} \
		OPT="${CFLAGS}" 
}

do_install() {
	# make install needs the original Makefile, or otherwise the inclues would
	# go to ${D}${STAGING...}/...
	install -m 0644 Makefile.orig Makefile

	install -d ${D}${libdir}/pkgconfig
	install -d ${D}${libdir}/python${PYTHON_MAJMIN}/config

	# rerun the build once again with original makefile this time
	# run install in a separate step to avoid compile/install race
	oe_runmake HOSTPGEN=${STAGING_BINDIR_NATIVE}/python3-native/pgen \
		HOSTPYTHON=${STAGING_BINDIR_NATIVE}/python3-native/python3 \
		STAGING_LIBDIR=${STAGING_LIBDIR} \
		STAGING_INCDIR=${STAGING_INCDIR} \
		STAGING_BASELIBDIR=${STAGING_BASELIBDIR} \
		BUILD_SYS=${BUILD_SYS} HOST_SYS=${HOST_SYS} \
		LIB=${baselib} \
		ARCH=${TARGET_ARCH} \
		DESTDIR=${D} LIBDIR=${libdir}
	
	oe_runmake HOSTPGEN=${STAGING_BINDIR_NATIVE}/python3-native/pgen \
		HOSTPYTHON=${STAGING_BINDIR_NATIVE}/python3-native/python3 \
		STAGING_LIBDIR=${STAGING_LIBDIR} \
		STAGING_INCDIR=${STAGING_INCDIR} \
		STAGING_BASELIBDIR=${STAGING_BASELIBDIR} \
		BUILD_SYS=${BUILD_SYS} HOST_SYS=${HOST_SYS} \
		LIB=${baselib} \
		ARCH=${TARGET_ARCH} \
		DESTDIR=${D} LIBDIR=${libdir} install

	# avoid conflict with 2to3 from Python 2
	rm -f ${D}/${bindir}/2to3

	install -m 0644 Makefile.sysroot ${D}/${libdir}/python${PYTHON_MAJMIN}/config/Makefile

	install -m 0644 ${S}/debian/sitecustomize.py.in ${D}/${libdir}/python${PYTHON_MAJMIN}/sitecustomize.py
	

	oe_multilib_header python${PYTHON_BINABI}/pyconfig.h

	# install file follow file list of package idle-python3.4
	mv ${D}${bindir}/idle3.4 ${D}${bindir}/idle-python3.4
	rm -rf ${D}${bindir}/idle3

	# install file follow file list of package libpython3.4	
	LINKLIB=$(basename $(readlink ${D}${libdir}/libpython3.4m.so))
	rm ${D}${libdir}/libpython3.4m.so
	ln -s ../${LINKLIB} ${D}${libdir}/python3.4/libpython3.4m.so
	ln -s ../${LINKLIB} ${D}${libdir}/python3.4/libpython3.4.so
	ln -s ${LINKLIB} ${D}${libdir}/libpython3.4m.so.1

	ln -s python3.4m ${D}${includedir}/python3.4
	
	install -d ${D}${sysconfdir}/python3.4
	cp ${D}${libdir}/python3.4/sitecustomize.py ${D}${sysconfdir}/python3.4/
	
	install -m 0755 ${S}/Tools/i18n/pygettext.py ${D}${bindir}/pygettext3.4
	ln -s ../lib/python3.4 ${D}${bindir}/pdb3.4 

	rm ${D}${bindir}/pydoc3
	rm ${D}${bindir}/pyvenv

	rm ${D}${bindir}/python3-config 
        rm ${D}${bindir}/python3 
	mv ${D}${libdir}/python3.4/config-3.4m- ${D}${libdir}/python3.4/config-3.4m
}

do_install_append_class-nativesdk () {
	create_wrapper ${D}${bindir}/python${PYTHON_MAJMIN} TERMINFO_DIRS='${sysconfdir}/terminfo:/etc/terminfo:/usr/share/terminfo:/usr/share/misc/terminfo:/lib/terminfo'
}

SSTATE_SCAN_FILES += "Makefile"
PACKAGE_PREPROCESS_FUNCS += "py_package_preprocess"

py_package_preprocess () {
	# copy back the old Makefile to fix target package
	install -m 0644 ${B}/Makefile.orig ${PKGD}/${libdir}/python${PYTHON_MAJMIN}/config/Makefile
	# Remove references to buildmachine paths in target Makefile and _sysconfigdata
	sed -i -e 's:--sysroot=${STAGING_DIR_TARGET}::g' -e s:'--with-libtool-sysroot=${STAGING_DIR_TARGET}'::g \
		${PKGD}/${libdir}/python${PYTHON_MAJMIN}/config/Makefile \
		${PKGD}/${libdir}/python${PYTHON_MAJMIN}/_sysconfigdata.py
}

require python-${PYTHON_MAJMIN}-manifest.inc

# manual dependency additions
RPROVIDES_${PN}-core = "${PN}"
RRECOMMENDS_${PN}-core = "${PN}-readline"
RRECOMMENDS_${PN}-crypt = "openssl"
RRECOMMENDS_${PN}-crypt_class-nativesdk = "nativesdk-openssl"

PACKAGES =+ "libpython3.4 idle-python3.4 python3.4 python3.4-dev python3.4-examples python3.4-minimal python3.4-venv "
FILES_idle-python3.4 += " ${bindir}/idle-python3.4 "

FILES_python3.4 = " \
	${bindir}/2to3-3.4 \
	${bindir}/pdb3.4 \
	${bindir}/pydoc3.4 \
	${bindir}/pygettext3.4 \
"
FILES_python3.4-dev = " \
	${bindir}/python3.4-config \
	${bindir}/python3.4m-config \
"

FILES_python3.4-examples = " ${libdir}/python3.4/turtledemo/* "

FILES_python3.4-minimal = " ${bindir}/python3.4*"

FILES_python3.4-venv = " \
	${bindir}/pyvenv-3.4 \
	${libdir}/python3.4/ensurepip/ \
"

# package libpython3
PACKAGES =+ " libpython3.4-staticdev libpython3.4-dev libpython3.4-minimal libpython3.4-stdlib libpython3.4-testsuite"

FILES_libpython3.4-staticdev += "${libdir}/python3.4/config-3.4m/libpython3.4m.a"

FILES_libpython3.4 = " \
	${libdir}/libpython*.so.* \
"

FILES_libpython3.4-dev = " \
	${includedir}/python3.4m/* \
	${includedir}/python3.4 \
	${libdir}/python3.4/config-3.4m/* \
	${libdir}/pkgconfig/* \
	${libdir}/python3.4/libpython3.4*.so \
"

FILES_libpython3.4-minimal = " \
	${sysconfdir}/python3.4/sitecustomize.py \
	${libdir}/python3.4/encodings/* \
	${libdir}/python3.4/__future__.py \
	${libdir}/python3.4/_bootlocale.py \
	${libdir}/python3.4/_collections_abc.py \
	${libdir}/python3.4/_compat_pickle.py \
	${libdir}/python3.4/_sitebuiltins.py \
	${libdir}/python3.4/_sysconfigdata.py \
	${libdir}/python3.4/_threading_local.py \
	${libdir}/python3.4/_weakrefset.py \
	${libdir}/python3.4/abc.py \
	${libdir}/python3.4/argparse.py \
	${libdir}/python3.4/ast.py \
	${libdir}/python3.4/base64.py \
	${libdir}/python3.4/bisect.py \
	${libdir}/python3.4/codecs.py \
	${libdir}/python3.4/collections/* \
	${libdir}/python3.4/enum.py \
	${libdir}/python3.4/fnmatch.py \
	${libdir}/python3.4/compileall.py \
	${libdir}/python3.4/configparser.py \
	${libdir}/python3.4/contextlib.py \
	${libdir}/python3.4/copy.py \
	${libdir}/python3.4/copyreg.py \
	${libdir}/python3.4/dis.py \
	${libdir}/python3.4/functools.py \
        ${libdir}/python3.4/genericpath.py \
	${libdir}/python3.4/getopt.py \
	${libdir}/python3.4/glob.py \
	${libdir}/python3.4/hashlib.py \
	${libdir}/python3.4/heapq.py \
	${libdir}/python3.4/imp.py \
	${libdir}/python3.4/importlib/* \
	${libdir}/python3.4/inspect.py \
	${libdir}/python3.4/io.py \
	${libdir}/python3.4/keyword.py \
	${libdir}/python3.4/lib-dynload/* \ 
	${libdir}/python3.4/linecache.py \
	${libdir}/python3.4/locale.py \
	${libdir}/python3.4/logging/* \
	${libdir}/python3.4/opcode.py \
	${libdir}/python3.4/operator.py \
	${libdir}/python3.4/optparse.py \
	${libdir}/python3.4/os.py \
	${libdir}/python3.4/pickle.py \
	${libdir}/python3.4/pkgutil.py \
	${libdir}/python3.4/_sysconfigdata_m.py \
	${libdir}/python3.4/platform.py \
	${libdir}/python3.4/posixpath.py \
	${libdir}/python3.4/py_compile.py \
	${libdir}/python3.4/random.py \
	${libdir}/python3.4/re.py \
	${libdir}/python3.4/reprlib.py \
	${libdir}/python3.4/runpy.py \
	${libdir}/python3.4/selectors.py \
	${libdir}/python3.4/site.py \
	${libdir}/python3.4/sitecustomize.py \
	${libdir}/python3.4/socket.py \
	${libdir}/python3.4/sre_compile.py \
	${libdir}/python3.4/sre_constants.py \
	${libdir}/python3.4/sre_parse.py \
	${libdir}/python3.4/ssl.py \
	${libdir}/python3.4/stat.py \
	${libdir}/python3.4/string.py \
	${libdir}/python3.4/stringprep.py \
	${libdir}/python3.4/struct.py \
	${libdir}/python3.4/subprocess.py \
	${libdir}/python3.4/sysconfig.py \
	${libdir}/python3.4/tempfile.py \
	${libdir}/python3.4/textwrap.py \
	${libdir}/python3.4/threading.py \
	${libdir}/python3.4/token.py \
	${libdir}/python3.4/tokenize.py \
	${libdir}/python3.4/traceback.py \
	${libdir}/python3.4/types.py \
	${libdir}/python3.4/warnings.py \
	${libdir}/python3.4/weakref.py \
"

FILES_libpython3.4-stdlib = " \
	${libdir}/python3.4/LICENSE.txt \
	${libdir}/python3.4/__phello__.foo.py \
	${libdir}/python3.4/_dummy_thread.py \
	${libdir}/python3.4/_markupbase.py \
	${libdir}/python3.4/_osx_support.py \
	${libdir}/python3.4/_pyio.py \
	${libdir}/python3.4/_strptime.py \
	${libdir}/python3.4/aifc.py \
	${libdir}/python3.4/antigravity.py \
	${libdir}/python3.4/asynchat.py \
	${libdir}/python3.4/asyncio/* \
	${libdir}/python3.4/asyncore.py \
	${libdir}/python3.4/bdb.py \
	${libdir}/python3.4/binhex.py \
	${libdir}/python3.4/bz2.py \
	${libdir}/python3.4/cProfile.py \
	${libdir}/python3.4/calendar.py \
	${libdir}/python3.4/cgi.py \
	${libdir}/python3.4/cgitb.py \
	${libdir}/python3.4/chunk.py \
	${libdir}/python3.4/cmd.py \
	${libdir}/python3.4/code.py \
	${libdir}/python3.4/codeop.py \
	${libdir}/python3.4/colorsys.py \
	${libdir}/python3.4/concurrent/* \
	${libdir}/python3.4/crypt.py \
	${libdir}/python3.4/csv.py \
	${libdir}/python3.4/ctypes/* \
	${libdir}/python3.4/curses/* \
	${libdir}/python3.4/datetime.py \
	${libdir}/python3.4/dbm/* \
	${libdir}/python3.4/decimal.py \
	${libdir}/python3.4/difflib.py \
	${libdir}/python3.4/distutils/* \
	${libdir}/python3.4/doctest.py \
	${libdir}/python3.4/dummy_threading.py \
	${libdir}/python3.4/email/* \
	${libdir}/python3.4/filecmp.py \
	${libdir}/python3.4/fileinput.py \
	${libdir}/python3.4/formatter.py \
	${libdir}/python3.4/fractions.py \
	${libdir}/python3.4/ftplib.py \
	${libdir}/python3.4/getpass.py \
	${libdir}/python3.4/gettext.py \
	${libdir}/python3.4/gzip.py \
	${libdir}/python3.4/hmac.py \
	${libdir}/python3.4/html/* \
	${libdir}/python3.4/http/* \
	${libdir}/python3.4/idlelib/* \
	${libdir}/python3.4/imaplib.py \
	${libdir}/python3.4/imghdr.py \
	${libdir}/python3.4/ipaddress.py \
	${libdir}/python3.4/json/* \
	${libdir}/python3.4/lib-dynload/* \
	${libdir}/python3.4/lib2to3/ \
	${libdir}/python3.4/lzma.py \
	${libdir}/python3.4/macpath.py \
	${libdir}/python3.4/macurl2path.py \
	${libdir}/python3.4/mailbox.py \
	${libdir}/python3.4/mailcap.py \
	${libdir}/python3.4/mimetypes.py \
	${libdir}/python3.4/modulefinder.py \
	${libdir}/python3.4/multiprocessing/* \
	${libdir}/python3.4/netrc.py \
	${libdir}/python3.4/nntplib.py \
	${libdir}/python3.4/ntpath.py \
	${libdir}/python3.4/nturl2path.py \
	${libdir}/python3.4/numbers.py \
	${libdir}/python3.4/pathlib.py \
	${libdir}/python3.4/pdb.py \
	${libdir}/python3.4/pickletools.py \
	${libdir}/python3.4/pipes.py \
	${libdir}/python3.4/plat-linux/* \
	${libdir}/python3.4/plistlib.py \
	${libdir}/python3.4/poplib.py \
	${libdir}/python3.4/pprint.py \
	${libdir}/python3.4/profile.py \
	${libdir}/python3.4/pstats.py \
	${libdir}/python3.4/pty.py \
	${libdir}/python3.4/pyclbr.py \
	${libdir}/python3.4/pydoc.py \
	${libdir}/python3.4/pydoc_data/* \
	${libdir}/python3.4/queue.py \
	${libdir}/python3.4/quopri.py \
	${libdir}/python3.4/rlcompleter.py \
	${libdir}/python3.4/sched.py \
	${libdir}/python3.4/shelve.py \
	${libdir}/python3.4/shlex.py \
	${libdir}/python3.4/smtpd.py \
	${libdir}/python3.4/shutil.py \
	${libdir}/python3.4/smtplib.py \
	${libdir}/python3.4/sndhdr.py \
	${libdir}/python3.4/socketserver.py \
	${libdir}/python3.4/sqlite3/* \
	${libdir}/python3.4/statistics.py \
	${libdir}/python3.4/sunau.py \
	${libdir}/python3.4/symbol.py \
	${libdir}/python3.4/symtable.py \
	${libdir}/python3.4/tabnanny.py \
	${libdir}/python3.4/tarfile.py \
	${libdir}/python3.4/telnetlib.py \
	${libdir}/python3.4/test/__init__.py \
	${libdir}/python3.4/test/pystone.py \
	${libdir}/python3.4/test/regrtest.py \
	${libdir}/python3.4/test/support/__init__.py \
	${libdir}/python3.4/this.py \
	${libdir}/python3.4/timeit.py \
	${libdir}/python3.4/tkinter/* \
	${libdir}/python3.4/trace.py \
	${libdir}/python3.4/tracemalloc.py \
	${libdir}/python3.4/tty.py \
	${libdir}/python3.4/turtle.py \
	${libdir}/python3.4/unittest/* \
	${libdir}/python3.4/urllib/* \
	${libdir}/python3.4/uu.py \
	${libdir}/python3.4/uuid.py \
	${libdir}/python3.4/venv/ \
	${libdir}/python3.4/wave.py \
	${libdir}/python3.4/webbrowser.py \
	${libdir}/python3.4/wsgiref/* \
	${libdir}/python3.4/xdrlib.py \
	${libdir}/python3.4/xml/ \
	${libdir}/python3.4/xmlrpc/* \
	${libdir}/python3.4/zipfile.py \
"

FILES_libpython3.4-testsuite = " \
	${libdir}/python3.4/ctypes/test/* \
	${libdir}/python3.4/distutils/tests/* \
	${libdir}/python3.4/idlelib/idle_test/* \
	${libdir}/python3.4/lib2to3/tests/* \
	${libdir}/python3.4/sqlite3/test/* \
	${libdir}/python3.4/test/* \
"

# catch debug extensions (isn't that already in python-core-dbg?)
FILES_${PN}-dbg += "${libdir}/python${PYTHON_MAJMIN}/lib-dynload/.debug"

# catch all the rest (unsorted)
PACKAGES += "${PN}-misc"
RDEPENDS_${PN}-misc += "${PN}-core"
FILES_${PN}-misc = "${libdir}/python${PYTHON_MAJMIN}"

# catch manpage
PACKAGES += "${PN}-man"
FILES_${PN}-man = "${datadir}/man"

BBCLASSEXTEND = "nativesdk"
