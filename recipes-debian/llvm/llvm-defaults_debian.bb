SUMMARY = "Low-Level Virtual Machine (LLVM)"
DESCRIPTION = "The Low-Level Virtual Machine (LLVM) is a collection of libraries and \
tools that make it easy to build compilers, optimizers, Just-In-Time \
code generators, and many other compiler-related programs."

inherit debian-package
PV = "0.25"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=30881d9361af6c3ee21030761456a5ec"

LLVM_VERSION = "3.5"

do_install() {
	# version number of the defaults package
	VDEF=${PV}

	VMAJOR=$(echo ${VDEF} | awk -F. '{print $1}')
	VMINOR=$(echo ${VDEF} | awk -F. '{print $2}' | sed -e 's/\([0-9]*\).*/\1/')
	REL_EXT=$(echo ${VDEF} | sed -e "s/^${VMAJOR}\.${VMINOR}//")

	# llvm-defaults 0.24 is the first version for 3.5.
	REL_NO_35=$(expr ${VMINOR} - 0)${REL_EXT}

	# complete version number, including the release
	CV_LLVM="1:${LLVM_VERSION}-${REL_NO_35}"

	# derived version number (without release)
	V_LLVM=$(echo ${CV_LLVM} | sed 's/^[0-9]*://' | sed 's/-[^-]*$//')

	# number for the package name
	PV_LLVM=$(echo ${V_LLVM} | awk -F. '{printf "%d.%d", $1, $2}')

	install -d ${D}${bindir}

	for bin in \
	    bugpoint llc llvm-ar llvm-as llvm-bcanalyzer llvm-config \
	    llvm-cov llvm-diff llvm-dis llvm-dwarfdump llvm-extract \
	    llvm-ld llvm-link llvm-mc llvm-nm llvm-objdump llvm-prof \
	    llvm-ranlib llvm-rtdyld llvm-size llvm-tblgen \
	    macho-dump opt ; do
		ln -sf ../lib/llvm-${PV_LLVM}/bin/$bin ${D}${bindir}/$bin
	done

	# llvm-runtime
	ln -sf ../lib/llvm-${PV_LLVM}/bin/lli ${D}${bindir}/lli

	# llvm-dev
	install -d ${D}${includedir} \
	           ${D}${libdir} \
	           ${D}${datadir}/vim/addons/syntax/ \
	           ${D}${datadir}/emacs/site-lisp

	ln -sf ../lib/llvm-${LLVM_VERSION}/include/llvm ${D}${includedir}/llvm
	ln -sf ../lib/llvm-${LLVM_VERSION}/include/llvm-c ${D}${includedir}/llvm-c
	ln -sf llvm-${LLVM_VERSION}.vim ${D}${datadir}/vim/addons/syntax/llvm.vim
	ln -sf tablegen-${LLVM_VERSION}.vim ${D}${datadir}/vim/addons/syntax/tablegen.vim
	ln -sf llvm-${LLVM_VERSION} ${D}${datadir}/emacs/site-lisp/llvm

	for lib in libLTO.so LLVMgold.so; do
		ln -sf llvm-${LLVM_VERSION}/lib/$lib ${D}${libdir}/$lib
	done

	# clang
	for bin in \
	    clang clang++ clang-check clang-tblgen c-index-test \
	    clang-apply-replacements clang-tidy pp-trace clang-query; do
		ln -sf ../lib/llvm-${PV_LLVM}/bin/$bin ${D}${bindir}/$bin
	done
	ln -sf scan-build-${PV_LLVM} ${D}${bindir}/scan-build
	ln -sf scan-view-${PV_LLVM} ${D}${bindir}/scan-view
	ln -sf asan_symbolize-${PV_LLVM} ${D}${bindir}/asan_symbolize
	ln -sf lldb-${PV_LLVM} ${D}${bindir}/lldb
}

PACKAGES = "clang lldb llvm-runtime llvm llvm-dev"

FILES_lldb = "${bindir}/lldb"
FILES_llvm-runtime = "${bindir}/lli"
FILES_llvm = "${bindir}/*"
FILES_llvm-dev = " \
    ${includedir} \
    ${libdir}/*.so \
    ${datadir}/vim \
    ${datadir}/emacs \
"
FILES_clang = " \
    ${bindir}/asan_symbolize \
    ${bindir}/c-index-test \
    ${bindir}/clang* \
    ${bindir}/pp-trace \
    ${bindir}/scan-* \
"

RDEPENDS_llvm += "llvm-runtime llvm-${LLVM_VERSION}"
RDEPENDS_llvm-runtime += "llvm-${LLVM_VERSION}-runtime"
RDEPENDS_llvm-dev += "llvm-${LLVM_VERSION}-dev"
RDEPENDS_clang += "clang-${LLVM_VERSION}"
RDEPENDS_lldb += "lldb-${LLVM_VERSION}" 
