SUMMARY = "thorough real-mode memory tester"
DESCRIPTION = "Memtest86+ scans your RAM for errors.\n\
.\n\
This tester runs independently of any OS - it is run at computer\n\
boot-up, so that it can test *all* of your memory.  You may want to\n\
look at `memtester', which allows to test your memory within Linux,\n\
but this one won't be able to test your whole RAM.\n\
.\n\
It can output a list of bad RAM regions usable by the BadRAM kernel\n\
patch, so that you can still use your old RAM with one or two bad bits.\n\
.\n\
Memtest86+ is based on memtest86 3.0, and adds support for recent\n\
hardware, as well as a number of general-purpose improvements,\n\
including many patches to memtest86 available from various sources.\n\
.\n\
Both memtest86 and memtest86+ are being worked on in parallel."
HOMEPAGE = "http://www.memtest.org/"

inherit debian-package
PV = "5.01"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://config.c;endline=9;md5=54ecc8028fdd7aabfcabdc42992d2cc2"

# Do-not-need-stub-32-header-to-compile_debian.patch:
#	Fix compile error on x86_64:
#	| fatal error: gnu/stubs-32.h: No such file or directory
#	This is causing the compiler to attempt a 32-bit compile: x86_64-deby-linux-gcc -m32
#	automatically including "gnu/stubs-32.h".
SRC_URI_x86-64 = "\
	${DEBIAN_SRC_URI} \
	file://Do-not-need-stub-32-header-to-compile_debian.patch"

DEBIAN_PATCH_TYPE = "nopatch"

inherit autotools-brokensep

DEPENDS += "cdrkit-native"

do_compile() {
	oe_runmake memtest.bin memtest CC="${CC} " LD="${LD} -z max-page-size=0x1000 --hash-style=gnu" AS="${AS} -32"
	./makeiso.sh
}

# Base on debian/rules
do_install() {
	install -D -m 0644 memtest.bin ${D}/boot/memtest86+.bin
	install -D -m 0644 memtest ${D}${libdir}/memtest86+/memtest86+.elf
	install -D -m 0644 mt*.iso ${D}${libdir}/memtest86+/memtest86+.iso
	install -D -m 0644 -s memtest_shared ${D}/boot/memtest86+_multiboot.bin
	install -D -m 0755 debian/grub ${D}${sysconfdir}/grub.d/20_memtest86+

	#install the lintian override
	install -d ${D}${datadir}/lintian/overrides
	install -m644 debian/lintian-overrides \
		${D}${datadir}/lintian/overrides/memtest86+
}
FILES_${PN} += "/boot ${datadir}/lintian/overrides"

# memtest86+.elf is ELF 32-bit LSB executable \
# add an INSANE_SKIP bypass check "arch" to avoid the QA error.
INSANE_SKIP_${PN} = "arch"

# Avoid WARNING: 'memtest86+_multiboot.bin' has relocations in .text [textrel].
# The fix us usually to compile as position independent code using the pic
# compiler option, but the test.c files can't compile with -fPIC:
#	|test.c:352:25: error: inconsistent operand constraints in an 'asm'
#	|                       asm __volatile__ (
#	|                        ^
INSANE_SKIP_${PN} += "textrel"

PARALLEL_MAKE = ""
