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
