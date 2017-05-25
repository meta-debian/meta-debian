#
# base recipe: meta-openembedded/meta-oe/recipes-core/llvm/llvm3.3_3.3.bb
# base branch: jethro
#

SUMMARY = "Low-Level Virtual Machine (LLVM)"
HOMEPAGE = "http://www.llvm.org/"

inherit debian-package
PV = "3.5"
PR = "r2"

# 3-clause BSD-like
# University of Illinois/NCSA Open Source License
LICENSE = "NCSA"
LIC_FILES_CHKSUM = "file://LICENSE.TXT;md5=47e311aa9caedd1b3abf098bd7814d1d"

DEPENDS = "isl libffi libxml2-native zlib binutils python libedit swig-native dpkg-native"

inherit perlnative pythonnative autotools

LLVM_VERSION_FULL = "${PV}.0"
SONAME_EXT = "1"
opt_flags = "-g -O2"
opt_flags_append_arm = " -marm"
EXTRA_OECONF = " \
    --prefix=${libdir}/llvm-${PV} \
    --bindir=\${prefix}/bin/ \
    --disable-assertions \
    --enable-shared \
    --enable-optimized \
    --with-optimize-option=' ${opt_flags}' \
    --enable-pic \
    --enable-libffi \
    --libdir=\${prefix}/lib \
    --with-binutils-include=${STAGING_INCDIR} \
    CLANG_VENDOR=Debian"
EXTRA_OECONF += " \
    --enable-keep-symbols \
    --program-prefix="" \
"

EXTRA_OEMAKE += "REQUIRES_RTTI=1 VERBOSE=1 CLANG_VENDOR=Debian DEBUGMAKE=1"

export HOST_SYS
export BUILD_SYS
export STAGING_INCDIR
export STAGING_LIBDIR

do_configure_prepend(){
	# Base on debian/rules
	cd ${S}
	DEBIAN_REVISION=`dpkg-parsechangelog |  sed -rne "s,^Version: 1:([0-9.]+)(~|-)(.*),\3,p"`
	cd -
	mkdir -p ${S}/clang/include/clang/Debian
	sed -e "s|@DEB_PATCHSETVERSION@|$DEBIAN_REVISION|" \
		${S}/debian/debian_path.h > ${S}/clang/include/clang/Debian/debian_path.h
	# Remove some old symlinks
	cd ${S}/tools/
	if test -h clang; then
		rm clang
	fi
	ln -s ../clang .
	readlink clang

	if test -h lldb; then
		rm lldb
	fi
	ln -s ../lldb .

	cd ${S}/projects/
	if test -h compiler-rt; then
		rm compiler-rt
	fi
	ln -s ../compiler-rt .
	readlink compiler-rt
	# Due to bug upstream, no symlink here
	cp -R -H ${S}/clang-tools-extra ${S}/tools/clang/tools/extra

	# Remove RPATHs
	sed -i 's:$(RPATH) -Wl,$(\(ToolDir\|LibDir\|ExmplDir\))::g' ${S}/Makefile.rules

	# Fix paths in llvm-config
	sed -i "s|sys::path::parent_path(CurrentPath))\.str()|sys::path::parent_path(sys::path::parent_path(CurrentPath))).str()|g" \
	       ${S}/tools/llvm-config/llvm-config.cpp

	# Fix the hardcoded libdir in llvm-config
	sed -i 's:/lib\>:${base_libdir}:g' ${S}/tools/llvm-config/llvm-config.cpp
	cd ${B}
}

do_compile_prepend() {
	cd ${B}
	# Fix libdir for multilib
	sed -i 's:(PROJ_prefix)/lib:(PROJ_prefix)${base_libdir}:g' Makefile.config

	oe_runmake \
		AR="${BUILD_AR}" \
		CC="${BUILD_CC}" \
		CFLAGS="${BUILD_CFLAGS}" \
		CXX="${BUILD_CXX}" \
		CXXFLAGS="${BUILD_CXXFLAGS}" \
		CPP="${BUILD_CPP}" \
		CPPFLAGS="${BUILD_CPPFLAGS}" \
		NM="${BUILD_NM}" \
		RANLIB="${BUILD_RANLIB}" \
		PATH="${STAGING_BINDIR_NATIVE}:$PATH" \
		cross-compile-build-tools
}

do_install_append() {
	# llvm-config-host only run on host for cross compiling
	mv ${D}${libdir}/llvm-${PV}/bin/llvm-config-host ${WORKDIR}

	# Base on debian/rules
	chrpath -d ${B}/Release/bin/clang
	chrpath -d `find ${D}${libdir}/llvm-${PV}/bin/ -type f -executable`

	# Add the trailing soname
	mv ${D}${libdir}/llvm-${PV}/lib/libLLVM-${PV}.so \
	       ${D}${libdir}/llvm-${PV}/lib/libLLVM-${PV}.so.${SONAME_EXT}

	cd ${D}${libdir}/llvm-${PV}/lib/
	mv    libclang.so                     libclang-${PV}.so.${SONAME_EXT}
	ln -s libclang-${PV}.so.${SONAME_EXT} libclang.so.${SONAME_EXT}
	ln -s libclang-${PV}.so.${SONAME_EXT} libclang-${PV}.so
	mv    liblldb.so                      liblldb-${PV}.so.${SONAME_EXT}
	ln -s liblldb-${PV}.so.${SONAME_EXT}  liblldb.so.${SONAME_EXT}
	rm -f libLLVM-${LLVM_VERSION_FULL}.so
	cd -

	install -d ${D}${bindir}

	# Create this fake directory to make the install libclang-common-dev happy
	# under the unsupported archs of compiler-rt
	install -d ${D}${libdir}/clang/${PV} \
	           ${D}${libdir}/llvm-${PV}/lib/clang/${LLVM_VERSION_FULL}/lib/ \
	           ${D}${libdir}/llvm-${PV}/lib/clang/${PV}/lib/clang_linux/
	mkdir -p ${B}/tools/clang/runtime/compiler-rt/clang_linux/

	# idem for the lldb python binding
	mkdir -p ${D}${libdir}/llvm-${PV}/lib/${PYTHON_DIR}/site-packages/lldb/

	# Rename binaries
	cd ${D}${bindir}; rm -f *
	for f in ../lib/llvm-${PV}/bin/*; do
		ln -s $f `basename $f`-${PV}
	done
	cd -

	cp ${S}/compiler-rt/lib/asan/scripts/asan_symbolize.py ${D}${bindir}/asan_symbolize-${PV}

	# Rename some stuff with the version name
	cp ${B}/tools/clang/docs/tools/clang.1      ${B}/tools/clang/docs/tools/clang-${PV}.1
	cp ${S}/clang/tools/scan-build/scan-build.1 ${S}/clang/tools/scan-build/scan-build-${PV}.1
	cp -f ${S}/utils/vim/llvm.vim               ${S}/utils/vim/llvm-${PV}.vim
	cp -f ${S}/utils/vim/tablegen.vim           ${S}/utils/vim/tablegen-${PV}.vim
	cp -f ${S}/clang/tools/clang-format/clang-format-diff.py \
	          ${S}/clang/tools/clang-format/clang-format-diff-${PV}
	cp -f ${S}/clang/tools/clang-format/clang-format.py \
	          ${S}/clang/tools/clang-format/clang-format-${PV}.py
	rm -rf ${S}/clang/tools/scan-build-${PV}
	cp -fR ${S}/clang/tools/scan-build ${S}/clang/tools/scan-build-${PV}
	rm -rf ${S}/clang/tools/scan-view-${PV}
	cp -fR ${S}/clang/tools/scan-view  ${S}/clang/tools/scan-view-${PV}

	# Managed in lldb-X.Y.links.in
	rm -f ${B}/Release/lib/python*/site-packages/lldb/_lldb.so

	# According to debian/llvm-X.Y-dev.dirs.in
	install -d ${D}${libdir}/llvm-${PV}/build \
	           ${D}${datadir}/emacs/site-lisp/llvm-${PV}

	# According to debian/clang-X.Y.install.in
	install -d ${D}${datadir}/clang
	cp -r ${S}/tools/clang/tools/scan-build-${PV} ${D}${datadir}/clang/
	cp -r ${S}/tools/clang/tools/scan-view-${PV}  ${D}${datadir}/clang/

	# According to debian/clang-format-X.Y.install.in
	install -d ${D}${datadir}/vim/addons/syntax/ \
	           ${D}${datadir}/emacs/site-lisp/clang-format-${PV}/
	cp ${S}/clang/tools/clang-format/clang-format-${PV}.py   ${D}${datadir}/vim/addons/syntax/
	cp ${S}/clang/tools/clang-format/clang-format-diff-${PV} ${D}${bindir}
	cp ${S}/clang/tools/clang-format/clang-format.el \
	       ${D}${datadir}/emacs/site-lisp/clang-format-${PV}/

	# According to debian/libclang-X.Y-dev.install.in
	cp -r ${B}/tools/clang/runtime/compiler-rt/clang_linux/ \
	          ${D}${libdir}/llvm-${PV}/lib/clang/${PV}/lib/

	# According to debian/libclang1-X.Y.install.in
	mv ${D}${libdir}/llvm-${PV}/lib/libclang-${PV}.so.1 ${D}${libdir}/

	# According to debian/liblldb-X.Y.install.in
	mv ${D}${libdir}/llvm-${PV}/lib/liblldb-${PV}.so.1  ${D}${libdir}/

	# According to debian/libllvmX.Y.install.in
	mv ${D}${libdir}/llvm-${PV}/lib/libLLVM-${PV}.so.1  ${D}${libdir}/

	# According to debian/llvm-X.Y-dev.install.in
	install -d ${D}${datadir}/llvm-${PV}/cmake \
	           ${D}${includedir}/llvm-${PV} \
	           ${D}${includedir}/llvm-c-${PV}
	mv ${D}${libdir}/llvm-${PV}/include/llvm/            ${D}${includedir}/llvm-${PV}/
	mv ${D}${libdir}/llvm-${PV}/include/llvm-c/          ${D}${includedir}/llvm-c-${PV}/
	mv ${D}${libdir}/llvm-${PV}/share/llvm/cmake/*.cmake ${D}${datadir}/llvm-${PV}/cmake/
	rm -rf ${D}${libdir}/llvm-${PV}/share/llvm
	cp ${B}/Makefile.common              ${D}${libdir}/llvm-${PV}/build/
	cp ${B}/Makefile.config              ${D}${libdir}/llvm-${PV}/build/
	cp ${B}/config.status                ${D}${libdir}/llvm-${PV}/build/
	cp ${S}/Makefile.rules               ${D}${libdir}/llvm-${PV}/build/
	cp ${S}/configure                    ${D}${libdir}/llvm-${PV}/build/
	cp -r ${S}/autoconf/                 ${D}${libdir}/llvm-${PV}/build/
	cp ${S}/utils/vim/llvm-${PV}.vim     ${D}${datadir}/vim/addons/syntax/
	cp ${S}/utils/vim/tablegen-${PV}.vim ${D}${datadir}/vim/addons/syntax/
	cp ${S}/utils/emacs/emacs.el         ${D}${datadir}/emacs/site-lisp/llvm-${PV}/
	cp ${S}/utils/emacs/llvm-mode.el     ${D}${datadir}/emacs/site-lisp/llvm-${PV}/
	cp ${S}/utils/emacs/tablegen-mode.el ${D}${datadir}/emacs/site-lisp/llvm-${PV}/

	# Remove some license files
	rm -f ${D}${libdir}/llvm-${PV}/include/llvm/Support/LICENSE.TXT \
	      ${D}${libdir}/llvm-${PV}/build/autoconf/LICENSE.TXT

	# According to debian/llvm-X.Y-runtime.install.in
	sed -e "s|@LLVM_VERSION@|${PV}|g" ${S}/debian/llvm-X.Y-runtime.binfmt.in \
	       > ${S}/debian/llvm-${PV}-runtime.binfmt
	install -d ${D}${datadir}/binfmts/
	cp ${S}/debian/llvm-${PV}-runtime.binfmt ${D}${datadir}/binfmts/

	# According to debian/llvm-X.Y-tools.install.in
	install -d ${D}${libdir}/llvm-${PV}/build/unittests \
	           ${D}${libdir}/llvm-${PV}/build/utils/lit
	cp ${S}/unittests/Makefile.unittest ${D}${libdir}/llvm-${PV}/build/unittests/
	cp -r ${S}/utils/lit/*              ${D}${libdir}/llvm-${PV}/build/utils/lit/

	# According to debian/python-clang-X.Y.install.in
	install -d ${D}${libdir}/${PYTHON_DIR}/dist-packages
	cp -r ${S}/tools/clang/bindings/python/clang/ ${D}${libdir}/${PYTHON_DIR}/dist-packages/

	# According to debian/clang-X.Y.links.in
	ln -sf ${datadir}/clang/scan-build-${PV}/scan-build  ${D}${bindir}/scan-build-${PV}
	ln -sf ${datadir}/clang/scan-view-${PV}/scan-view    ${D}${bindir}/scan-view-${PV}

	# According to debian/libclang-X.Y-dev.links.in
	ln -sf libclang-${PV}.so.1       ${D}${libdir}/libclang-${PV}.so
	ln -sf ../../libclang-${PV}.so.1 ${D}${libdir}/llvm-${PV}/lib/libclang.so

	# According to debian/libclang-common-X.Y-dev.links.in
	install -d ${D}${includedir}/clang/${PV} \
	           ${D}${includedir}/clang/${LLVM_VERSION_FULL} \
	           ${D}${libdir}/clang/${LLVM_VERSION_FULL}
	ln -sf ../../../lib/llvm-${PV}/lib/clang/${LLVM_VERSION_FULL}/include \
	        ${D}${includedir}/clang/${PV}/include
	ln -sf ../../llvm-${PV}/lib/clang/${LLVM_VERSION_FULL}/include \
	        ${D}${libdir}/clang/${PV}/include
	ln -sf ../../llvm-${PV}/lib/clang/${LLVM_VERSION_FULL}/lib \
	        ${D}${libdir}/clang/${PV}/lib
	ln -sf ../../../lib/llvm-${PV}/lib/clang/${LLVM_VERSION_FULL}/include \
	        ${D}${includedir}/clang/${LLVM_VERSION_FULL}/include
	ln -sf ../../llvm-${PV}/lib/clang/${LLVM_VERSION_FULL}/include \
	        ${D}${libdir}/clang/${LLVM_VERSION_FULL}/include
	ln -sf ../../llvm-${PV}/lib/clang/${LLVM_VERSION_FULL}/lib \
	        ${D}${libdir}/clang/${LLVM_VERSION_FULL}/lib

	# According to debian/libclang1-X.Y.links.in
	# as upstream
	ln -sf ../../libclang-${PV}.so.1 ${D}${libdir}/llvm-${PV}/lib/libclang-${PV}.so.1
	# Compatibility for the ABI breakage (See #762959)
	ln -sf libclang-${PV}.so.1 ${D}${libdir}/libclang.so.1

	# According to debian/liblldb-X.Y.links.in
	install -d ${D}${libdir}/${PYTHON_DIR}/dist-packages/lldb-${PV}
	ln -sf liblldb-${PV}.so.1        ${D}${libdir}/liblldb-${PV}.so
	ln -sf ../../../liblldb-${PV}.so ${D}${libdir}/${PYTHON_DIR}/dist-packages/lldb-${PV}/_lldb.so
	ln -sf ../../liblldb-${PV}.so.1  ${D}${libdir}/llvm-${PV}/lib/liblldb.so.1

	# According to debian/llvm-X.Y-dev.links.in
	install -d ${D}${libdir}/llvm-${PV}/build \
	           ${D}${libdir}/${PYTHON_DIR}/dist-packages
	ln -sf ../../libLLVM-${PV}.so.1 ${D}${libdir}/llvm-${PV}/lib/libLLVM-${PV}.so
	ln -sf ../../libLLVM-${PV}.so.1 ${D}${libdir}/llvm-${PV}/lib/libLLVM-${LLVM_VERSION_FULL}.so.1
	ln -sf ../../libLLVM-${PV}.so.1 ${D}${libdir}/llvm-${PV}/lib/libLLVM-${LLVM_VERSION_FULL}.so
	ln -sf libLLVM-${PV}.so.1 ${D}${libdir}/libLLVM-${LLVM_VERSION_FULL}.so.1
	ln -sf ../../../include/llvm-c-${PV}/llvm-c ${D}${libdir}/llvm-${PV}/include/llvm-c
	ln -sf ../../../include/llvm-${PV}/llvm     ${D}${libdir}/llvm-${PV}/include/llvm
	ln -sf ../include/       ${D}${libdir}/llvm-${PV}/build/include
	ln -sf ../../llvm-${PV}/ ${D}${libdir}/llvm-${PV}/build/Release
	ln -sf ../../llvm-${PV}/ ${D}${libdir}/llvm-${PV}/build/Debug+Asserts

	# According to debian/python-lldb-X.Y.links.in
	ln -sf ../../llvm-${PV}/lib/${PYTHON_DIR}/site-packages/lldb/ \
	        ${D}${libdir}/${PYTHON_DIR}/dist-packages/lldb
	ln -sf ../../../libLLVM-${LLVM_VERSION_FULL}.so.1 \
	        ${D}${libdir}/${PYTHON_DIR}/dist-packages/lldb/libLLVM-${LLVM_VERSION_FULL}.so.1
	ln -sf ../../../libLLVM-${LLVM_VERSION_FULL}.so.1 \
	        ${D}${libdir}/${PYTHON_DIR}/dist-packages/lldb/libLLVM-${PV}.so.1

	# Correct files permission
	chmod 0644 ${D}${libdir}/llvm-${PV}/lib/*.a
}

SYSROOT_PREPROCESS_FUNCS += "llvm_sysroot_preprocess"
llvm_sysroot_preprocess() {
	install -d ${SYSROOT_DESTDIR}${bindir_crossscripts}
	mv ${WORKDIR}/llvm-config-host ${SYSROOT_DESTDIR}${bindir_crossscripts}/llvm-config${PV}
}

# we name and ship packages as Debian,
# so we need pass QA errors with dev-so and dev-deps
INSANE_SKIP_python-lldb-${PV} += "dev-so"
INSANE_SKIP_liblldb-${PV} += "dev-so"
INSANE_SKIP_lldb-${PV} += "dev-deps"
INSANE_SKIP_llvm-${PV}-tools += "dev-deps"
INSANE_SKIP_clang-${PV} += "dev-deps"
INSANE_SKIP_python-lldb-${PV} += "dev-deps"

PACKAGES =+ "clang-format-${PV} clang-modernize-${PV} libclang-${PV} libclang-${PV}-dev \
             libclang-common-${PV}-dev python-clang-${PV} clang-${PV} \
             libllvm${PV} llvm-${PV} llvm-${PV}-runtime llvm-${PV}-tools llvm-${PV}-dev \
             lldb-${PV} liblldb-${PV} python-lldb-${PV} liblldb-${PV}-dev \
             "
PACKAGES =+ "libclang-${PV}-staticdev llvm-${PV}-staticdev liblldb-${PV}-staticdev"

FILES_libclang-${PV}-staticdev = " \
    ${libdir}/llvm-${PV}/lib/libclang*.a \
    ${libdir}/llvm-${PV}/lib/libmodernizeCore.a \
"
FILES_llvm-${PV}-staticdev = " \
    ${libdir}/llvm-${PV}/lib/libLLVM*.a \
    ${libdir}/llvm-${PV}/lib/libllvm*.a \
    ${libdir}/llvm-${PV}/lib/libLTO*.a \
"
FILES_liblldb-${PV}-staticdev = "${libdir}/llvm-${PV}/lib/liblldb*.a"

FILES_clang-format-${PV} = " \
    ${bindir}/clang-format-* \
    ${libdir}/llvm-${PV}/bin/clang-format \
    ${datadir}/*/*/*/clang-format* \
"
FILES_clang-modernize-${PV} = " \
    ${bindir}/clang-modernize-${PV} \
    ${libdir}/llvm-${PV}/bin/clang-modernize \
"
FILES_libclang-${PV} = " \
    ${libdir}/libclang*${SOLIBS} \
    ${libdir}/llvm-${PV}/lib/libclang*${SOLIBS} \
"
FILES_libclang-${PV}-dev = " \
    ${libdir}/libclang-${PV}${SOLIBSDEV} \
    ${libdir}/llvm-${PV}/include/clang* \
    ${libdir}/llvm-${PV}/lib/libclang*${SOLIBSDEV} \
"
FILES_libclang-common-${PV}-dev = " \
    ${includedir}/clang \
    ${libdir}/clang \
    ${libdir}/llvm-${PV}/lib/clang \
"
FILES_python-clang-${PV} = " \
    ${libdir}/${PYTHON_DIR}/*-packages/clang \
"
FILES_clang-${PV} = " \
    ${bindir}/asan_symbolize-${PV} \
    ${bindir}/c-index-test-${PV} \
    ${bindir}/clang* \
    ${bindir}/pp-trace-${PV} \
    ${bindir}/scan-*${PV} \
    ${libdir}/llvm-${PV}/bin/clang* \
    ${libdir}/llvm-${PV}/bin/c-index-test \
    ${libdir}/llvm-${PV}/bin/pp-trace \
    ${datadir}/clang \
"
FILES_libllvm${PV} = "${libdir}/libLLVM*${SOLIBS}"
FILES_llvm-${PV} = " \
    ${bindir}/bugpoint-${PV} \
    ${bindir}/llc-${PV} \
    ${bindir}/llvm-*-${PV} \
    ${bindir}/macho-dump-${PV} \
    ${bindir}/opt-${PV} \
    ${libdir}/llvm-${PV}/bin/bugpoint \
    ${libdir}/llvm-${PV}/bin/llc \
    ${libdir}/llvm-${PV}/bin/llvm-* \
    ${libdir}/llvm-${PV}/bin/macho-dump \
    ${libdir}/llvm-${PV}/bin/opt \
"
FILES_llvm-${PV}-runtime = " \
    ${bindir}/lli*${PV} \
    ${libdir}/llvm-${PV}/bin/lli* \
    ${datadir}/binfmts \
"
FILES_llvm-${PV}-tools = " \
    ${bindir}/FileCheck-${PV} \
    ${bindir}/count-${PV} \
    ${bindir}/not-${PV} \
    ${libdir}/llvm-${PV}/bin/FileCheck \
    ${libdir}/llvm-${PV}/bin/count \
    ${libdir}/llvm-${PV}/bin/not \
    ${libdir}/llvm-${PV}/build/unittests \
    ${libdir}/llvm-${PV}/build/utils \
"
FILES_llvm-${PV}-dev = " \
    ${includedir}/llvm-* \
    ${libdir}/llvm-${PV}/build \
    ${libdir}/llvm-${PV}/include/llvm* \
    ${libdir}/llvm-${PV}/lib/LLVM*${SOLIBSDEV} \
    ${libdir}/llvm-${PV}/lib/libLTO${SOLIBSDEV} \
    ${libdir}/llvm-${PV}/lib/BugpointPasses${SOLIBSDEV} \
    ${libdir}/llvm-${PV}/lib/libLLVM*.so* \
    ${libdir}/libLLVM*.so* \
    ${datadir}/emacs/site-lisp/llvm-${PV} \
    ${datadir}/llvm-${PV}/cmake \
    ${datadir}/vim/addons/syntax/ \
"
FILES_lldb-${PV} = " \
    ${bindir}/lldb-* \
    ${libdir}/llvm-${PV}/bin/lldb* \
"
FILES_liblldb-${PV} = " \
    ${libdir}/liblldb-${PV}.so* \
    ${libdir}/llvm-${PV}/lib/liblldb${SOLIBS} \
    ${libdir}/llvm-${PV}/lib/${PYTHON_DIR}/*-packages/readline.so \
    ${libdir}/${PYTHON_DIR}/*-packages/lldb-${PV} \
"
FILES_python-lldb-${PV} = " \
    ${libdir}/llvm-${PV}/lib/${PYTHON_DIR}/*-packages/lldb \
    ${libdir}/${PYTHON_DIR}/*-packages/lldb \
"
FILES_liblldb-${PV}-dev = " \
    ${libdir}/llvm-${PV}/include/lldb \
"

FILES_${PN}-dbg += " \
    ${libdir}/llvm-${PV}/*/.debug \
"
FILES_${PN}-doc += " \
    ${libdir}/llvm-${PV}/share/man \
    ${libdir}/llvm-${PV}/docs \
"

RDEPENDS_clang-${PV} += "libclang-common-${PV}-dev libclang-${PV} binutils"
RDEPENDS_clang-format-${PV} += "python"
RDEPENDS_clang-modernize-${PV} += "clang-${PV}"
RDEPENDS_libclang-${PV}-dev += "libclang-common-${PV}-dev"
RDEPENDS_libclang-common-${PV}-dev += "libllvm${PV}"
RDEPENDS_python-clang-3.5 += "python"
RDEPENDS_llvm-${PV} += "llvm-${PV}-runtime"
RDEPENDS_llvm-${PV}-runtime += "binfmt-support"
RDEPENDS_llvm-${PV}-tools += "python llvm-${PV}-dev"
RDEPENDS_lldb-${PV} += "libllvm${PV} python llvm-${PV}-dev python-lldb-${PV}"
RDEPENDS_liblldb-${PV} += "llvm-${PV}"
RDEPENDS_python-lldb-${PV} += "python"
RDEPENDS_liblldb-${PV} += "lldb-${PV}"

DEBIANNAME_libclang-${PV} = "libclang1-${PV}"
DEBIAN_NOAUTONAME_libllvm${PV} = "1"
DEBIAN_NOAUTONAME_liblldb-${PV} = "1"
