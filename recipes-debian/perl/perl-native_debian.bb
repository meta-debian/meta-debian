require recipes-devtools/perl/perl-native_5.14.3.bb
require perl.inc

DPR = "1"

# Remove patches: 
# Already existed in source code:
# 	debian/errno_ver.diff (already exist in debian/patches)
# \
# Source code was changed, not found the content to patch:
# 	perl-build-in-t-dir.patch
#	native-nopacklist.patch
SRC_URI += " \
file://Configure-multilib.patch \
file://perl-configpm-switch.patch \
file://native-perlinc.patch \
file://MM_Unix.pm.patch \
file://dynaloaderhack.patch \
file://perl-5.14.3-fix-CVE-2010-4777.patch \
"

do_install_prepend (){
	# fakethr.h and perlsfio.h not exist in perl-5.20
	# so temporary create them for pass do_install.
	# These files will be removed in do_install_append
	touch fakethr.h perlsfio.h
}

do_install_append (){
        #Install patchlevel-debian.h
        install patchlevel-debian.h ${D}${libdir}/perl/${PV}/CORE

	# Remove fakethr.h and perlsfio.h
	if [ -f ${D}${libdir}/perl/${PV}/CORE/fakethr.h ]; then
		 rm ${D}${libdir}/perl/${PV}/CORE/fakethr.h
	fi
	
	if [ -f ${D}${libdir}/perl/${PV}/CORE/perlsfio.h ]; then
		 rm ${D}${libdir}/perl/${PV}/CORE/perlsfio.h
        fi
}
