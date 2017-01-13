
# Base recipe: meta/recipes-rt/rt-tests/rt-tests_0.92.bb
# Base branch: master
#

SUMMARY = "Real-Time preemption testcases"
HOMEPAGE = "https://rt.wiki.kernel.org/index.php/Cyclictest"

PR = "r0"

inherit debian-package
PV = "0.89"

LICENSE = "GPLv2 & GPLv2+"
LIC_FILES_CHKSUM = "\
file://COPYING;md5=751419260aa954499f7abaabaa882bbe \
file://src/cyclictest/cyclictest.c;beginline=7;endline=9;md5=e768b8da44555fe63f65e5c497844cb5 \
file://src/pi_tests/pi_stress.c;beginline=6;endline=19;md5=bd426a634a43ec612e9fbf125dfcc949"

DEPENDS = "linux-libc-headers virtual/libc"

# calling 'uname -m' is broken on crossbuilds
EXTRA_OEMAKE = "NUMA=0"

do_install() {
	oe_runmake install DESTDIR=${D} SBINDIR=${sbindir} MANDIR=${mandir} \
	INCLUDEDIR=${includedir}
}

PACKAGES += "backfire-dkms"
FILES_backfire-dkms = "${prefix}/src/backfire"

inherit ptest

SRC_URI += "file://run-ptest \
            file://rt_bmark.py \ 
           "

do_install_ptest() {
        cp ${WORKDIR}/rt_bmark.py ${D}${PTEST_PATH}  
}                                                                               
                                                                                
RDEPENDS_${PN}-ptest += " stress python python-subprocess python-multiprocessing \
			  python-datetime python-re python-lang"
