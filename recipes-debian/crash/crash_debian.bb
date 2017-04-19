#
# base recipe: meta-openembedded/meta-oe/recipes-kernel/crash/crash_7.1.3.bb
# base commit: 0e3749e33f1c3bf920f34b4e5afd0208ff32fec7
#

SUMMARY = "kernel debugging utility, allowing gdb like syntax"
DESCRIPTION = "The core analysis suite is a self-contained tool that can be used to\n\
investigate either live systems, or multiple different core dump formats\n\
including kdump, LKCD, netdump and diskdump.\n\
.\n\
o  The tool is loosely based on the SVR4 crash command, but has been\n\
   completely integrated with gdb in order to be able to display\n\
   formatted kernel data structures, disassemble source code, etc.\n\
.\n\
o  The current set of available commands consist of common kernel core\n\
   analysis tools such as a context-specific stack traces, source code\n\
   disassembly, kernel variable displays, memory display, dumps of\n\
   linked-lists, etc.  In addition, any gdb command may be entered,\n\
   which in turn will be passed onto the gdb module for execution.\n\
.\n\
o  There are several commands that delve deeper into specific kernel\n\
   subsystems, which also serve as templates for kernel developers\n\
   to create new commands for analysis of a specific area of interest.\n\
   Adding a new command is a simple affair, and a quick recompile\n\
   adds it to the command menu.\n\
.\n\
o  The intent is to make the tool independent of Linux version dependencies,\n\
   building in recognition of major kernel code changes so as to adapt to\n\
   new kernel versions, while maintaining backwards compatibility."

PR = "r0"

inherit debian-package
PV = "7.0.8"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING3;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = "zlib readline"

# There is no debian patch, but debian/rules keep using quilt.
# debian/rules - line12:
# 	dh $@ --with quilt
DEBIAN_PATCH_TYPE = "quilt"
# Empty DEBIAN_QUILT_PATCHES to avoid error "debian/patches not found"
DEBIAN_QUILT_PATCHES = ""

# 0001-cross_add_configure_option.patch:
# 	Add option for cross compile to ./configure command
# 7001force_define_architecture.patch:
# 	Fix error: conflicting types for 'size_t'
SRC_URI += " \
    file://0001-cross_add_configure_option.patch \
    file://7001force_define_architecture.patch \
"

inherit gettext

# crash 7.1.3 and before don't support mips64
COMPATIBLE_HOST = "^(?!mips64).*"

EXTRA_OEMAKE = 'RPMPKG="${PV}" \
                GDB_TARGET="${TARGET_SYS}" \
                GDB_HOST="${BUILD_SYS}" \
                GDB_MAKE_JOBS="${PARALLEL_MAKE}" \
                '

do_compile_prepend() {
	case ${TARGET_ARCH} in
		aarch64*)    ARCH=ARM64 ;;
		arm*)        ARCH=ARM ;;
		i*86*)       ARCH=X86 ;;
		x86_64*)     ARCH=X86_64 ;;
		powerpc64*)  ARCH=PPC64 ;;
		powerpc*)    ARCH=PPC ;;
		mips*)       ARCH=MIPS ;;
	esac

	sed -i s/FORCE_DEFINE_ARCH/"${ARCH}"/g ${S}/configure.c
	sed -i -e 's:#define TARGET_CFLAGS_ARM_ON_X86_64.*:#define TARGET_CFLAGS_ARM_ON_X86_64\t\"TARGET_CFLAGS=-D_FILE_OFFSET_BITS=64\":g' \
	          ${S}/configure.c
	sed -i 's/&gt;/>/g' ${S}/Makefile
}

do_compile() {
	oe_runmake
}

do_install() {
	install -d ${D}${bindir} \
	           ${D}${mandir}/man8 \
	           ${D}${includedir}/${DPN} \

	oe_runmake DESTDIR=${D} install
	install -m 0644 ${S}/crash.8 ${D}${mandir}/man8
	install -m 0644 ${S}/defs.h ${D}${includedir}/${DPN}/
}

RDEPENDS_${PN} += "liblzma"

# Causes gcc to get stuck and eat all available memory in qemuarm builds
ARM_INSTRUCTION_SET = "arm"
