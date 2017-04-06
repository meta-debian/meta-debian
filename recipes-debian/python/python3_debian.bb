#
# base recipe: meta/recipes-devtools/python/python3_3.4.3.bb
# base branch: jethro
# 

require python3.inc

DEPENDS = "python3-native libffi bzip2 db gdbm openssl readline sqlite3 zlib virtual/libintl xz-utils expat mpdecimal"
PR = "${INC_PR}.3"

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
SDK_CC_ARCH += "-DNDEBUG -fno-inline"
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

do_configure_prepend() {
	# Correct MULTIARCH variable, not use "$CC --print-multiarch" command,
	# result of this command will be empty when gcc don't support multiarch.
	if [ "${TARGET_ARCH}" = "arm" -o "${TARGET_ARCH}" = "armeb" ]; then
		_MULTIARCH="${TARGET_ARCH}-${TARGET_OS}"
	else
		_MULTIARCH="${TARGET_ARCH}-${TARGET_OS}-gnu"
	fi
	sed -i -e "s|^MULTIARCH=.*|MULTIARCH=${_MULTIARCH}|g" ${S}/configure.ac

	# Correct path to install pkgconfig files
	sed -i -e "s|^LIBPC=.*|LIBPC=\$(LIBDIR)/pkgconfig|" ${S}/Makefile.pre.in
}
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
		-e 's,^SCRIPTDIR=.*,SCRIPTDIR=${STAGING_BASELIBDIR},g' \
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

	oe_multilib_header python${PYTHON_BINABI}/pyconfig.h

	# install file follow file list of package idle-python3.4
	mv ${D}${bindir}/idle3.4 ${D}${bindir}/idle-python3.4
	rm -rf ${D}${bindir}/idle3

	# install file follow file list of package libpython3.4	and libpython3.4-dev
	LINKLIB=$(basename $(readlink ${D}${libdir}/libpython3.4m.so))
	cd ${D}${libdir}/python3.4/config-${PYTHON_BINABI}*
	ln -s ../../${LINKLIB} libpython3.4m.so
	ln -s ../../${LINKLIB} libpython3.4.so
	cd -
	ln -s ${LINKLIB} ${D}${libdir}/libpython3.4m.so.1

	ln -s python3.4m ${D}${includedir}/python3.4

	# Install sitecustomize.py
	install -d ${D}${sysconfdir}/python3.4
	cp ${S}/debian/sitecustomize.py.in \
		${D}${sysconfdir}/python${PYTHON_MAJMIN}/sitecustomize.py
	ln -s ../../../${sysconfdir}/python${PYTHON_MAJMIN}/sitecustomize.py \
		${D}${libdir}/python${PYTHON_MAJMIN}/

	#
	# Base on debian/rules
	#
	VER=${PYTHON_MAJMIN}
	PVER=python${VER}
	PRIORITY=$(echo ${VER} | tr -d '.')0
	scriptdir=${libdir}/python${VER}

	for f in ${S}/debian/*.in; do
		f2=`echo $f | sed "s,PVER,${PVER},g;s/@VER@/${VER}/g;s,\.in$,,"`;
		if [ $f2 != ${S}/debian/control ]; then
			sed -e "s/@PVER@/${PVER}/g;s/@VER@/${VER}/g;s/@SVER@/${SVER}/g" \
			    -e "s/@PRIORITY@/${PRIORITY}/g" \
			    -e "s,@SCRIPTDIR@,/${scriptdir},g" \
			    -e "s,@HOST_QUAL@,:${HOST_ARCH},g" \
			  <$f >$f2
		fi
	done

	# remove files, which are not packaged
	rm -rf ${D}${libdir}/python${VER}/ctypes/macholib
	rm -f ${D}/$scriptdir/plat-*/regen
	rm -f ${D}/$scriptdir/lib2to3/*.pickle
	rm -f ${D}${mandir}/man1/python3.1

	# cannot build it, zlib maintainer won't provide a mingw build
	find ${D} -name 'wininst*.exe' | xargs -r rm -f

	# fix some file permissions
	for i in runpy fractions lib2to3/refactor tkinter/tix; do
		chmod a-x ${D}/$scriptdir/$i.py
	done
	chmod a-x ${D}/$scriptdir/test/test_pathlib.py

	cp ${S}/Misc/python.man ${D}${mandir}/man1/python${VER}.1
	ln -sf python${VER}.1 ${D}${mandir}/man1/python${VER}m.1
	cp ${S}/debian/pydoc.1 ${D}${mandir}/man1/pydoc${VER}.1

	# Symlinks to /usr/bin for some tools
	ln -sf ../lib/python${VER}/pdb.py ${D}${bindir}/pdb${VER}
	cp ${S}/debian/pdb.1 ${D}${mandir}/man1/pdb${VER}.1
	cp ${S}/debian/2to3-3.1 ${D}${mandir}/man1/2to3-${VER}.1
	cp ${S}/debian/pysetup3.1 ${D}${mandir}/man1/pysetup${VER}.1
	cp ${S}/debian/pyvenv3.1 ${D}${mandir}/man1/pyvenv-${VER}.1

	# versioned install only
	for i in 2to3 idle3 pydoc3 pysetup3 python3 python3-config; do
		rm -f ${D}${bindir}/$i
	done
	rm -f ${D}${libdir}/pkgconfig/python3.pc

	cp ${S}/Tools/i18n/pygettext.py ${D}${bindir}/pygettext${VER}
	cp ${S}/debian/pygettext.1 ${D}${mandir}/man1/pygettext${VER}.1

	# test_ctypes fails with test_macholib.py installed
	rm -f ${D}/$scriptdir/ctypes/test/test_macholib.py
	# test_bdist_wininst fails, '*.exe' files are not installed
	rm -f ${D}/$scriptdir/distutils/tests/test_bdist_wininst.py

	# fixed upstream ...
	chmod -x ${D}/$scriptdir/test/test_dbm_gnu.py
	chmod -x ${D}/$scriptdir/test/test_dbm_ndbm.py

	install -d ${D}${docdir}/python${VER}/examples
	cp -r ${S}/Tools/* ${D}${docdir}/python${VER}/examples/
	rm -rf ${D}${docdir}/python${VER}/examples/Tools/buildbot
	rm -rf ${D}${docdir}/python${VER}/examples/Tools/msi
	# We don't need rgb.txt, we'll use our own:
	rm -rf ${D}${docdir}/python${VER}/examples/Tools/pynche/X

	# IDLE
	test -f ${D}${bindir}/idle${VER} && mv ${D}${bindir}/idle${VER} ${D}${bindir}/idle-python${VER}
	rm -f ${D}${libdir}/python${VER}/idlelib/idle.bat
	cp ${S}/debian/idle-${PVER}.1 ${D}${mandir}/man1/

	rm -f ${D}${bindir}/python
	rm -f ${D}${bindir}/pyvenv
}

do_install_append_class-nativesdk () {
	create_wrapper ${D}${bindir}/python${PYTHON_MAJMIN} TERMINFO_DIRS='${sysconfdir}/terminfo:/etc/terminfo:/usr/share/terminfo:/usr/share/misc/terminfo:/lib/terminfo'
}

SSTATE_SCAN_FILES += "Makefile"
PACKAGE_PREPROCESS_FUNCS += "py_package_preprocess"

py_package_preprocess () {
	# Remove references to buildmachine paths in target Makefile and _sysconfigdata
	sed -i -e 's:--sysroot=${STAGING_DIR_TARGET}::g' -e s:'--with-libtool-sysroot=${STAGING_DIR_TARGET}'::g \
		${PKGD}/${libdir}/python${PYTHON_MAJMIN}/config-${PYTHON_BINABI}*/Makefile \
		${PKGD}/${libdir}/python${PYTHON_MAJMIN}/_sysconfigdata.py
}

SYSROOT_PREPROCESS_FUNCS += "postgresql_sysroot_preprocess"
postgresql_sysroot_preprocess () {
	install -D -m 0644 ${B}/Makefile.sysroot \
		${SYSROOT_DESTDIR}${libdir}/python${PYTHON_MAJMIN}/config-${PYTHON_BINABI}-${HOST_SYS}/Makefile
}
require python-${PYTHON_MAJMIN}-manifest.inc

# manual dependency additions
RPROVIDES_${PN}-core = "${PN}"
RRECOMMENDS_${PN}-core = "${PN}-readline"
RRECOMMENDS_${PN}-crypt = "openssl"
RRECOMMENDS_${PN}-crypt_class-nativesdk = "nativesdk-openssl"

FILES_${PN}-2to3 += "${bindir}/2to3-${PYTHON_MAJMIN}"
FILES_${PN}-pydoc += "${bindir}/pydoc${PYTHON_MAJMIN} ${bindir}/pydoc3"

PACKAGES =+ "${PN}-pyvenv"
FILES_${PN}-pyvenv += "${bindir}/pyvenv-${PYTHON_MAJMIN} \
                       ${libdir}/python${PYTHON_MAJMIN}/ensurepip/* \
                       "

# package libpython3
PACKAGES =+ "libpython3 libpython3-staticdev"
FILES_libpython3 = "${libdir}/libpython*.so.*"
FILES_libpython3-staticdev += "${libdir}/python${PYTHON_MAJMIN}/config-${PYTHON_BINABI}*/libpython${PYTHON_BINABI}.a"

# catch debug extensions (isn't that already in python-core-dbg?)
FILES_${PN}-dbg += "${libdir}/python${PYTHON_MAJMIN}/lib-dynload/.debug"

# catch manpage
PACKAGES += "${PN}-man"
FILES_${PN}-man = "${datadir}/man"

#
# Provide packages as Debian
# but keep compatible with poky
#

# Get list file of libpython3.4-minimal
python do_package_prepend() {
    import re, os

    dpn = d.getVar("DPN", True) or ""
    s = d.getVar("S", True) or ""
    libdir = d.getVar("libdir", True) or ""
    sysconfdir = d.getVar("sysconfdir", True) or ""
    python_majmin = d.getVar("PYTHON_MAJMIN", True) or ""
    scriptdir = "%s/python%s" % (libdir, python_majmin)

    # Get package name of libpython3.4-minimal which depends on target building or nativesdk building:
    #     libpython3.4-minimal
    #     nativesdk-libpython3.4-minimal
    #     libpython3.4-minimal-nativesdk
    p_lmin = ""
    pattern_lmin = ".*lib%s-minimal.*" % dpn
    packages = d.getVar("PACKAGES", True) or ""
    arr_packages = packages.split()
    for pkg in arr_packages:
        if re.match(pattern_lmin,pkg):
            p_lmin = pkg
            break

    readme_lmin = "%s/debian/PVER-minimal.README.Debian.in" % s
    files_lmin = d.getVar("FILES_%s" % p_lmin, True) or ""

    # get list file from debian/PVER-minimal.README.Debian.in
    # base on debian/rule (line71 - line85)
    patern_mods = '^[ ]+\\w*\\t*module\\s*'
    patern_exts = '^[ ]+\\w*\\t*extension\\w*\\s*'
    patern_packages = '^[ ]+\\w*\\t*package\\s*'
    min_mods = []
    min_exts = []
    min_packages = []
    min_encodings = []

    with open(readme_lmin, 'r') as f:
        lines = f.readlines()
        for line in lines:
            sline = line.split()
            if re.match(patern_mods,line):
                min_mods.append(sline[0])
            if re.match(patern_exts,line):
                min_exts.append(sline[0])
            if re.match(patern_packages,line):
                min_packages.append(sline[0])

    filterout_encodings = ["big5.*", "bz2.*", "cp932.py", "cp949.py", \
                        "cp950.py", "euc_.*", "gb.*" "iso2022.*", \
                        "johab.py", "shift_jis.*"]
    patern_encodings = "(" + ")|(".join(filterout_encodings) + ")"
    for i in os.listdir("%s/Lib/encodings" % s):
        if i.endswith(".py") and not re.match(patern_encodings,i):
            min_encodings.append("encodings/%s" % i)
    min_encodings.append("codecs.py")
    min_encodings.append("stringprep.py")

    # base on debian/rule (line841 - line851)
    for i in min_mods:
        files_lmin += " %s/%s.py" % (scriptdir, i)
    for i in min_packages:
        files_lmin += " %s/%s" % (scriptdir, i)
    for i in min_encodings:
        files_lmin += " %s/%s" % (scriptdir, i)
    for i in min_exts:
        files_lmin += " %s/lib-dynload/%s.*.so" % (scriptdir, i)

    files_lmin += " \
            %s/site.py \
            %s/_sysconfigdata.py \
            %s/plat-*/_sysconfigdata_m.py \
            %s/python%s/sitecustomize.py \
            %s/python%s/sitecustomize.py" \
        % (scriptdir,scriptdir,scriptdir,scriptdir,python_majmin,sysconfdir,python_majmin)
    d.setVar("FILES_%s" % p_lmin,files_lmin)
}

PACKAGES =+ "${DPN}-minimal ${DPN}-dev idle-${DPN} lib${DPN}-testsuite"
PACKAGES += "${DPN}-examples lib${DPN}-minimal lib${DPN}-stdlib"

FILES_${DPN}-minimal = " \
    ${bindir}/python${PYTHON_MAJMIN} \
    ${bindir}/python${PYTHON_BINABI} \
"
FILES_${DPN}-dev = "${bindir}/python*-config"
FILES_lib${DPN}-testsuite = " \
    ${libdir}/python${PYTHON_MAJMIN}/*/tests \
    ${libdir}/python${PYTHON_MAJMIN}/ctypes/test \
    ${libdir}/python${PYTHON_MAJMIN}/idlelib/idle_test \
    ${libdir}/python${PYTHON_MAJMIN}/tkinter/test \
    ${libdir}/python${PYTHON_MAJMIN}/unittest/test \
"
FILES_${PN}-core = "${bindir}/*"
FILES_${PN}-dev += " \
    ${libdir}/python${PYTHON_MAJMIN}/*.so \
    ${libdir}/python${PYTHON_MAJMIN}/config-${PYTHON_BINABI}* \
    ${libdir}/*/pkgconfig \
"
FILES_${DPN}-examples = " \
    ${libdir}/python${PYTHON_MAJMIN}/turtledemo \
    ${docdir}/python${PYTHON_MAJMIN}/examples \
"
FILES_lib${DPN}-stdlib = "${libdir}/python${PYTHON_MAJMIN}"
FILES_idle-${DPN} = "${bindir}/idle-python${PYTHON_MAJMIN}"

DEBIAN_NOAUTONAME_lib${DPN}-minimal = "1"
DEBIAN_NOAUTONAME_lib${DPN}-stdlib = "1"
DEBIAN_NOAUTONAME_lib${DPN}-testsuite = "1"

# python3-pyvenv as python3.4-venv
RPROVIDES_${PN}-pyvenv += "${DPN}-venv"
DEBIANNAME_${PN}-pyvenv = "${DPN}-venv"
# libpython3 as libpython3.4
RPROVIDES_libpython3 += "lib${DPN}"
DEBIANNAME_libpython3 = "lib${DPN}"
# python3-dev as libpython3.4-dev
RPROVIDES_${PN}-dev += "lib${DPN}-dev"
DEBIANNAME_${PN}-dev = "lib${DPN}-dev"
# python3-core as python3.4
RPROVIDES_${PN}-core += "${DPN}"
DEBIANNAME_${PN}-core = "${DPN}"
# python3-misc as libpython3.4-stdlib
RPROVIDES_lib${DPN}-stdlib = "${PN}-misc"

RDEPENDS_lib${DPN}-minimal += " \
    ${PN}-codecs ${PN}-compile ${PN}-crypt ${PN}-io ${PN}-importlib \
    ${PN}-lang ${PN}-logging ${PN}-math ${PN}-netclient ${PN}-pickle \
    ${PN}-pkgutil ${PN}-re ${PN}-reprlib ${PN}-textutils ${PN}-threading \
    ${PN}-shell ${PN}-stringold ${PN}-subprocess \
"
RDEPENDS_lib${DPN}-stdlib += " \
    ${PN}-2to3 ${PN}-asyncio ${PN}-audio ${PN}-codecs ${PN}-compression \
    ${PN}-ctypes ${PN}-curses ${PN}-datetime ${PN}-db ${PN}-debugger \
    ${PN}-difflib ${PN}-distutils ${PN}-doctest ${PN}-email ${PN}-html \
    ${PN}-idle ${PN}-image ${PN}-io ${PN}-json ${PN}-lang ${PN}-mailbox \
    ${PN}-math ${PN}-mime ${PN}-mmap ${PN}-multiprocessing ${PN}-netclient \
    ${PN}-netserver ${PN}-numbers ${PN}-pickle ${PN}-pprint ${PN}-profile \
    ${PN}-pydoc ${PN}-readline ${PN}-resource ${PN}-shell ${PN}-smtpd \
    ${PN}-sqlite3 ${PN}-terminal ${PN}-textutils ${PN}-threading \
    ${PN}-tkinter ${PN}-unittest ${PN}-unixadmin ${PN}-xml \
"

# python3-core as python3.4
# python3-pyvenv as python3.4-venv
# python3-dev as libpython3.4-dev
RDEPENDS_${PN}-core += "${DPN}-minimal lib${DPN}-stdlib"
RDEPENDS_${PN}-pyvenv += "${DPN}"
RDEPENDS_lib${DPN}-stdlib += "lib${DPN}-minimal"
RDEPENDS_${DPN}-minimal += "lib${DPN}-minimal"
RDEPENDS_lib${DPN} += "lib${DPN}-stdlib"
RDEPENDS_${DPN}-examples += "${DPN}"
RDEPENDS_${DPN}-dev += "${DPN} lib${DPN}-dev lib${DPN}"
RDEPENDS_${PN}-dev += "lib${DPN}-stdlib"
RDEPENDS_lib${DPN}-testsuite += "${DPN} ${PN}-tests ${PN}-sqlite3-tests"
RDEPENDS_idle-${DPN} += "${DPN}"

PARALLEL_MAKE = ""
BBCLASSEXTEND = "nativesdk"
