SUMMARY = "GNU implementation of the IPMI protocol"
DESCRIPTION = "	GNU implementation of the IPMI protocol - common files 		\
		FreeIPMI is a collection of Intelligent Platform Management 	\
		IPMI system software. It provides in-band and out-of-band 	\
		software and a development library conforming to the Intelligent\ 
		Platform Management Interface (IPMI v1.5 and v2.0) standards."
HOMEPAGE = "http://www.gnu.org/software/freeipmi/"

PR = "r3"
inherit debian-package
PV = "1.4.5"

LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

EXTRA_OECONF += "--without-random-device"

inherit autotools
#libgcrypt required to build libfreeipmi
DEPENDS += "libgcrypt chrpath-native"

#Split the freeipmi to sub-packages list
PACKAGES =+ 	" ${PN}-bmc-watchdog 	\
		${PN}-common 		\
		${PN}-ipmidetect 	\
		${PN}-ipmiseld 		\
		${PN}-tools 		\
		libfreeipmi-dev 	\
		libfreeipmi16 		\
		libipmiconsole-dev 	\
		libipmiconsole2 	\
		libipmidetect-dev 	\
		libipmidetect0 		\
		libipmimonitoring-dev 	\
		libipmimonitoring5a	\
"
#Install follow Debian jessies
do_install_append() {
	mv ${D}${sysconfdir}/sysconfig ${D}${sysconfdir}/default
	install -d ${D}${sysconfdir}/logrotate.d
	install -m 0644 ${S}/debian/freeipmi-bmc-watchdog.bmc-watchdog.logrotate \
		  	${D}${sysconfdir}/logrotate.d/bmc-watchdog
	install -m 0644 ${S}/debian/freeipmi-ipmidetect.ipmidetectd.default \
			${D}${sysconfdir}/default/ipmidetectd
	install -m 0755 ${S}/debian/freeipmi-ipmidetect.ipmidetectd.init \
			${D}${sysconfdir}/init.d/ipmidetectd
	install -m 0755 ${S}/debian/freeipmi-bmc-watchdog.bmc-watchdog.init \
			${D}${sysconfdir}/init.d/bmc-watchdog
	install -m 0755 ${S}/debian/freeipmi-ipmiseld.ipmiseld.init \
			${D}${sysconfdir}/init.d/ipmiseld

	#change permission
	LINKLIB=$(basename $(readlink ${D}${libdir}/libfreeipmi.so))
	chmod 0644 ${D}${libdir}/${LINKLIB}
	
	LINKLIB=$(basename $(readlink ${D}${libdir}/libipmiconsole.so))
	chmod 0644 ${D}${libdir}/${LINKLIB}
	
	LINKLIB=$(basename $(readlink ${D}${libdir}/libipmimonitoring.so))
	chmod 0644 ${D}${libdir}/${LINKLIB}

	LINKLIB=$(basename $(readlink ${D}${libdir}/libipmidetect.so))
	chmod 0644 ${D}${libdir}/${LINKLIB}

	#Correct the softlinks
	[ -L ${D}${sbindir}/ipmi-console ] && rm ${D}${sbindir}/ipmi-console
	ln -s ipmiconsole ${D}${sbindir}/ipmi-console
	
	[ -L ${D}${sbindir}/ipmi-detect ] && rm ${D}${sbindir}/ipmi-detect
	ln -s ipmidetect ${D}${sbindir}/ipmi-detect
	
	[ -L ${D}${sbindir}/ipmi-ping ] && rm ${D}${sbindir}/ipmi-ping
	ln -s ipmiping ${D}${sbindir}/ipmi-ping
	
	[ -L ${D}${sbindir}/ipmi-power ] && rm ${D}${sbindir}/ipmi-power
	ln -s ipmipower ${D}${sbindir}/ipmi-power

	[ -L ${D}${sbindir}/pef-config ] && rm ${D}${sbindir}/pef-config
	ln -s ipmi-pef-config ${D}${sbindir}/pef-config
	
	[ -L ${D}${sbindir}/rmcp-ping ] && rm ${D}${sbindir}/rmcp-ping
	ln -s rmcpping ${D}${sbindir}/rmcp-ping
	
	rm ${D}${libdir}/*.la

	#change the rpath or runpath in binaries files
	for file in $(find ${D}${sbindir} -type f -exec file {} \; | \
		grep ELF | grep executable | cut -d: -f1); do
		chrpath -d $file
	done
}
#correct the sub-package names
DEBIANNAME_libipmimonitoring5a = "libipmimonitoring5a"

#shipment file to packages
FILES_${PN}-bmc-watchdog = "${sysconfdir}/default/bmc-watchdog 		\
			    ${sysconfdir}/init.d/bmc-watchdog 		\
			    ${sysconfdir}/logrotate.d/bmc-watchdog 	\
			    ${sbindir}/bmc-watchdog"

FILES_${PN}-common = "${sysconfdir}/freeipmi/freeipmi.conf 		\
		      ${sysconfdir}/freeipmi/freeipmi_interpret_sel.conf \
		      ${sysconfdir}/freeipmi/freeipmi_interpret_sensor.conf"

FILES_${PN}-ipmidetect = "${sysconfdir}/default/ipmidetectd 		\
			  ${sysconfdir}/freeipmi/ipmidetect.conf 	\
			  ${sysconfdir}/freeipmi/ipmidetectd.conf 	\
			  ${sysconfdir}/init.d/ipmidetectd 		\
			  ${sbindir}/ipmi-detect 			\
			  ${sbindir}/ipmidetect 			\
			  ${sbindir}/ipmidetectd"

FILES_${PN}-ipmiseld = "${sysconfdir}/freeipmi/ipmiseld.conf 		\
			${sysconfdir}/init.d/ipmiseld 			\
			${sbindir}/ipmiseld"

FILES_${PN}-tools = "${sbindir}/* 					\
		     ${localstatedir}/*"

FILES_libfreeipmi-dev = "${includedir}/freeipmi/* 			\
			 ${libdir}/pkgconfig/libfreeipmi.pc 		\
			 ${libdir}/libfreeipmi.so"

FILES_libfreeipmi16 = "${libdir}/libfreeipmi.so.*"

FILES_libipmiconsole-dev = "${includedir}/ipmiconsole.h 		\
			    ${libdir}/pkgconfig/libipmiconsole.pc 	\
			    ${libdir}/libipmiconsole.so"

FILES_libipmiconsole2 = "${sysconfdir}/freeipmi/libipmiconsole.conf 	\
			 ${libdir}/libipmiconsole.so.2*"

FILES_libipmidetect-dev = "${includedir}/ipmidetect.h 			\
			   ${libdir}/pkgconfig/libipmidetect.pc 	\
			   ${libdir}/libipmidetect.so"

FILES_libipmidetect0 = "${libdir}/libipmidetect.so.*"

FILES_libipmimonitoring-dev =  "${includedir}/ipmi_monitoring* 		\
				${libdir}/pkgconfig/libipmimonitoring.pc \
				${libdir}/libipmimonitoring.so"

FILES_libipmimonitoring5a = "${libdir}/libipmimonitoring.so.*"

#follow debian/control
RDEPENDS_${PN}-tools 		+= "${PN}-common"
RDEPENDS_${PN}-bmc-watchdog 	+= "${PN}-common ${PN}-tools"
RDEPENDS_${PN}-ipmidetect 	+= "${PN}-common"
RDEPENDS_libfreeipmi16 		+= "${PN}-common"
RDEPENDS_libfreeipmi-dev 	+= "libfreeipmi16 ${PN}-common"
RDEPENDS_libipmidetect0 	+= "${PN}-common"
RDEPENDS_libipmidetect-dev 	+= "libipmidetect0 ${PN}-common"
RDEPENDS_libipmimonitoring5a 	+= "${PN}-common"
RDEPENDS_libipmimonitoring-dev 	+= "libipmimonitoring5a ${PN}-common"
RDEPENDS_libipmiconsole2 	+= "${PN}-common"
RDEPENDS_libipmiconsole-dev 	+= "${PN}-common libipmiconsole2"               
