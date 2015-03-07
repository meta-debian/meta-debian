require recipes-core/busybox/${PN}_1.22.1.bb
FILESEXTRAPATHS_prepend = "\
${COREBASE}/meta/recipes-core/busybox/busybox:\
${COREBASE}/meta/recipes-core/busybox/files:\
"

inherit debian-package
DEBIAN_SECTION = "utils"
DPR = "0"

LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=de10de48642ab74318e893a61105afbb"

SRC_URI += " \
file://get_header_tar.patch \                                        
file://busybox-appletlib-dependency.patch \                          
file://busybox-udhcpc-no_deconfig.patch \                            
file://find-touchscreen.sh \                                         
file://busybox-cron \                                                
file://busybox-httpd \                                               
file://busybox-udhcpd \                                              
file://default.script \                                              
file://simple.script \                                               
file://hwclock.sh \                                                  
file://mount.busybox \                                               
file://syslog \                                                      
file://syslog-startup.conf \                                         
file://syslog.conf \                                                 
file://busybox-syslog.default \                                      
file://mdev \                                                        
file://mdev.conf \                                                   
file://umount.busybox \                                              
file://defconfig \                                                   
file://busybox-syslog.service.in \                                   
file://busybox-klogd.service.in \                                    
file://fail_on_no_media.patch \                                      
file://run-ptest \                                                   
file://inetd.conf \                                                  
file://inetd \                                                       
file://login-utilities.cfg \                                         
file://0001-build-system-Specify-nostldlib-when-linking-to-.o-fi.patch \
"

# To help find ncurses header and library for do_menuconfig functions
do_configure_prepend() {
	
	if test -f ${STAGING_INCDIR_NATIVE}/ncursesw/ncurses.h \
		-o test -f ${STAGING_INCDIR_NATIVE}/ncurses/ncurses.h; then
		sub_pair="/usr/include/:${STAGING_INCDIR_NATIVE}"
	elif test -f ${STAGING_INCDIR_NATIVE}/ncurses.h; then
		sub_pair="/usr/include/ncursesw:${STAGING_INCDIR_NATIVE}"
	fi

	sed -i -e "s:$sub_pair:g" \
			${S}/scripts/kconfig/lxdialog/check-lxdialog.sh
	# -B option to help gcc find library
	sed -i -e "s:\$cc:\$cc -B${STAGING_LIBDIR_NATIVE}:g" \
			${S}/scripts/kconfig/lxdialog/check-lxdialog.sh
	
}
