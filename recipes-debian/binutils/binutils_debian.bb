require recipes-devtools/binutils/binutils.inc
require recipes-devtools/binutils/binutils-2.24.inc

FILESEXTRAPATHS_prepend ="\
${COREBASE}/meta/recipes-devtools/binutils/binutils:\
"

inherit debian-package
DEBIAN_SECTION = "devel"
DPR = "0"
DEBIAN_PATCH_TYPE = "quilt"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM="\                                                             
	file://src-release;endline=17;md5=4830a9ef968f3b18dd5e9f2c00db2d35\         
	file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552\                        
	file://COPYING.LIB;md5=9f604d8a4f8e74f4f5140845a21b6674\                    
	file://COPYING3;md5=d32239bcb673463ab874e80d47fae504\                       
	file://COPYING3.LIB;md5=6a6a8e020838b23406c81b19c1d46df6\                   
	file://gas/COPYING;md5=d32239bcb673463ab874e80d47fae504\                    
	file://include/COPYING;md5=59530bdf33659b29e73d4adb9f9f6552\                
	file://include/COPYING3;md5=d32239bcb673463ab874e80d47fae504\               
	file://libiberty/COPYING.LIB;md5=a916467b91076e631dd8edb7424769c7\          
	file://bfd/COPYING;md5=d32239bcb673463ab874e80d47fae504\                    
"

# Patch binutils-uclibc-300-001_ld_makefile_patch.patch, 
# binutils-uclibc-300-012_check_ldrunpath_length.patch,
# fix-pr15815.patch, fix-pr2404.patch, fix-pr16476.patch, fix-pr16428.patch, 
# replace_macros_with_static_inline.patch, 
# 0001-Fix-MMIX-build-breakage-from-bfd_set_section_vma-cha.patch,
# binutils-uninitialised-warning.patch are no need
# since it has been applied in current version of source code.
#
# mips64-default-ld-emulation.patch, binutils-xlp-support.patch
# no need to apply since source code has changed.
SRC_URI += "\
	file://binutils-uclibc-100-uclibc-conf.patch \
	file://binutils-uclibc-300-006_better_file_error.patch \
	file://binutils-uclibc-gas-needs-libm.patch \
	file://libtool-2.4-update_debian.patch \
	file://libiberty_path_fix.patch \
	file://binutils-poison_debian.patch \
	file://libtool-rpath-fix_debian.patch \
	file://binutils-armv5e.patch \
	file://binutils_CVE-2014-8484_debian.patch \
	file://binutils_CVE-2014-8485.patch \
	file://binutils_CVE-2014-8501.patch \
	file://binutils_CVE-2014-8502_1.patch \
	file://binutils_CVE-2014-8502.patch \
	file://binutils_CVE-2014-8503.patch \
	file://binutils_CVE-2014-8504.patch \
	file://binutils_CVE-2014-8737_debian.patch \
"

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
                                                                                
BBCLASSEXTEND = "native"
