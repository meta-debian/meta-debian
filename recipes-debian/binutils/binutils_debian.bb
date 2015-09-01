#
# base recipe: meta/recipes-devtools/binutils/binutils_2.25.bb
# base branch: master
# base commit: 3b7c38458856805588d552508de10944ed38d9f2
#

require binutils.inc

PR = "${INC_PR}.0"

DEPENDS += "flex bison zlib"                                                    
                                                                                
EXTRA_OECONF += "--with-sysroot=/ \                                             
                --enable-install-libbfd \                                       
                --enable-install-libiberty \                                    
                --enable-shared \                                               
                "                                                               
                                                                                
EXTRA_OECONF_class-native = "--enable-targets=all \                             
                             --enable-64-bit-bfd \                              
                             --enable-install-libiberty \                       
                             --enable-install-libbfd"                           
                                                                                
do_install_class-native () {                                                    
        autotools_do_install                                                    
                                                                                
        # Install the libiberty header                                          
        install -d ${D}${includedir}                                            
        install -m 644 ${S}/include/ansidecl.h ${D}${includedir}                
        install -m 644 ${S}/include/libiberty.h ${D}${includedir}               
                                                                                
        # We only want libiberty, libbfd and libopcodes                         
        rm -rf ${D}${bindir}                                                    
        rm -rf ${D}${prefix}/${TARGET_SYS}                                      
        rm -rf ${D}${prefix}/lib/ldscripts                                      
        rm -rf ${D}${prefix}/share/info                                         
        rm -rf ${D}${prefix}/share/locale                                       
        rm -rf ${D}${prefix}/share/man                                          
        rmdir ${D}${prefix}/share || :                                          
        rmdir ${D}/${libdir}/gcc-lib || :                                       
        rmdir ${D}/${libdir}64/gcc-lib || :                                     
        rmdir ${D}/${libdir} || :                                               
        rmdir ${D}/${libdir}64 || :                                             
}                                                                               
                                                                                
BBCLASSEXTEND = "native nativesdk"
