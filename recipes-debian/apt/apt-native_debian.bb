require recipes-devtools/apt/apt-native_0.9.9.4.bb
FILESEXTRAPATHS_prepend = "\
${THISDIR}/files:${COREBASE}/meta/recipes-devtools/apt/files:\
${COREBASE}/meta/recipes-devtools/apt/apt-0.9.9.4:\
"

inherit debian-package
DEBIAN_SECTION = "admin"
DPR = "0"

DEPENDS += "db-native"

# Exclude inappropriate patch which for old version 
# db_linking_hack.patch
# Resolve conflict for noconfigure.patch and no-curl.patch
# Note that no-curl.patch make apt does not support https methods
# Patch file no-ko-translation_debian.patch should not be apply
# since all available translations should be mentioned.
SRC_URI += " \
file://use-host_debian.patch \
file://makerace.patch \
file://no-nls-dpkg.patch \
file://fix-gcc-4.6-null-not-defined.patch \
file://truncate-filename.patch \
file://nodoc.patch \
file://disable-configure-in-makefile.patch \
file://noconfigure_debian.patch \
file://no-curl_debian.patch \
file://apt.conf \
"
# Skip build test for native package
SRC_URI += " \
file://gtest-skip-fix.patch \
"

#PARALLEL_MAKE = ""

EXTRA_OECONF = " --with-cpus=1 --with-procs=1 --with-proc-multiply=1"

do_configure_prepend() {
	sed -i -e "s#AC_PATH_PROG(XSLTPROC,xsltproc)#\
		AC_PATH_PROG(XSLTPROC,xsltproc,[], [${STAGING_BINDIR_NATIVE}])#" \
		${S}/configure.ac
}
