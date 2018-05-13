PR = "r0"

inherit debian-package
PV = "2.13"
DPN = "nkf"

LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://../nkf.c;endline=22;md5=a30d8d09c708efb8fa61f6bedb1d6677"

S = "${WORKDIR}/git/NKF.mod"

inherit cpan

# 'libdir' can be changed to /usr/lib/<multiarch-triplet>,
# but get_perl_version() get config.sh with the path:
#   ${STAGING_LIBDIR}${PERL_OWN_DIR}/perl/config.sh
# Make get_perl_version() get the correct path of config.sh.
PERL_OWN_DIR_class-target = "/${@os.path.relpath(nonarch_libdir, libdir)}"

FILES_${PN} += "${nonarch_libdir}/perl"
