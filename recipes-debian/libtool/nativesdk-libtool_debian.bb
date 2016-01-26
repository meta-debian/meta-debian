#
# base-recipe: meta/recipes-devtools/libtool/nativesdk-libtool_2.4.2.bb
# base-branch: daisy
#
                                                                                
PR = "${INC_PR}.0"                                                              

require libtool.inc                                               

inherit nativesdk

SRC_URI += "\
file://prefix.patch \
file://fixinstall.patch \
" 

#S = "${WORKDIR}/git"
FILES_${PN} += "${datadir}/libtool/*"
                                           
do_configure_prepend () { 
        # Remove any existing libtool m4 since old stale versions would break
        # any upgrade   
        rm -f ${STAGING_DATADIR}/aclocal/libtool.m4
        rm -f ${STAGING_DATADIR}/aclocal/lt*.m4                             
}                                 
                                                                               
do_install () {
        autotools_do_install
        install -d ${D}${bindir}/
        install -m 0755 ${HOST_SYS}-libtool ${D}${bindir}/                      
}                                                                               
                                                                                
SYSROOT_PREPROCESS_FUNCS += "libtoolnativesdk_sysroot_preprocess"               
                                                                                
libtoolnativesdk_sysroot_preprocess () {                                        
        install -d ${SYSROOT_DESTDIR}${bindir_crossscripts}/                    
        install -m 755 ${D}${bindir}/${HOST_SYS}-libtool ${SYSROOT_DESTDIR}${bindir_crossscripts}/${HOST_SYS}-libtool
}                                                                               
                                                                                
export CONFIG_SHELL="/bin/bash" 
