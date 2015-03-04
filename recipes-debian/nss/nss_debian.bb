require recipes-support/nss/nss_3.15.1.bb
FILESEXTRAPATHS_prepend = "\
${COREBASE}/meta/recipes-support/nss/nss:\
${COREBASE}/meta/recipes-support/nss/files:\
"

inherit debian-package
DEBIAN_SECTION = "libs"
DPR = "0"

LICENSE = "MPLv1.1 GPLv2.0 LGPLv2.1"
                                                                                
LIC_FILES_CHKSUM = "file://nss/lib/freebl/mpi/doc/LICENSE;md5=491f158d09d948466afce85d6f1fe18f \
                    file://nss/lib/freebl/mpi/doc/LICENSE-MPL;md5=6bf96825e3d7ce4de25621ae886cc859"

SRC_URI += "\                                                                    
    file://nss-fix-support-cross-compiling.patch \                              
    file://nss-no-rpath-for-cross-compiling.patch \                             
    file://nss-fix-incorrect-shebang-of-perl.patch \                            
"
