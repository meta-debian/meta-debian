SUMMARY = "Tool for creating device nodes"
FILESEXTRAPATHS_prepend = "\
${THISDIR}/files:${COREBASE}/meta/recipes-core/busybox/busybox:\
${COREBASE}/meta/recipes-core/busybox/files:\
"

inherit debian-package
DEBIAN_SECTION = "utils"
PR = "r0"
DPR = "0"
DPN = "busybox"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=de10de48642ab74318e893a61105afbb"

SRC_URI += " \
	file://get_header_tar.patch\
	file://busybox-appletlib-dependency.patch\
	file://busybox-udhcpc-no_deconfig.patch\
	file://makedevsconfig\
	file://fail_on_no_media.patch\
	file://run-ptest\
	file://login-utilities.cfg\
	file://0001-build-system-Specify-nostldlib-when-linking-to-.o-fi.patch\
"

inherit cml1 native ptest                                          
                                                                                
do_prepare_config () {                                                          
        sed -e 's#@DATADIR@#${datadir}#g' \
                < ${WORKDIR}/makedevsconfig > ${S}/.config
}
                                                                                
do_configure () {                                                               
        do_prepare_config                                                       
        cml1_do_configure                                                       
}

do_compile() {
	unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
	oe_runmake busybox_unstripped
	cp busybox_unstripped busybox
	oe_runmake busybox.links 
}

do_install () {                                                                 
	install -d ${D}${base_bindir}                                   
	install -m 0755 ${B}/busybox ${D}${base_bindir}/makedevs
	install -d ${D}${datadir}                                               
        install -m 644 ${COREBASE}/meta/files/device_table-minimal.txt ${D}${datadir}/
}                                                                               
