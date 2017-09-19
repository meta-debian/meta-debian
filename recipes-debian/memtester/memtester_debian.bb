SUMMARY = "Utility for testing the memory subsystem"
DESCRIPTION = "This is a userspace utility for testing the memory subsystem for faults.\n\
In comparison to memtest86 you do not need to reboot the computer to test\n\
for memory faults.\n\
.\n\
Memtester can also be told to test memory starting at a particular\n\
physical address."
HOMEPAGE = "http://pyropus.ca/software/memtester/"

inherit debian-package
PV = "4.3.0"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3"

do_compile() {
	echo '${CC} ${CFLAGS} -DPOSIX -c' > conf-cc
	echo '${CC} ${LDFLAGS}' > conf-ld
	oe_runmake
}

do_install() {
	install -D -m 0755 memtester ${D}${sbindir}/memtester
}
