require recipes-devtools/perl/perl_5.14.3.bb
require perl.inc

FILESEXTRAPATHS_prepend = "${THISDIR}/files:"

DPR = "0"

# Remove the patches which already available in source code:
# 	debian/cpan_definstalldirs.diff 
# 	debian/db_file_ver.diff
# 	debian/doc_info.diff 
# 	debian/enc2xs_inc.diff
# 	debian/errno_ver.diff
#	debian/fixes/respect_umask.diff
#	debian/writable_site_dirs.diff 
# 	debian/extutils_set_libperl_path.diff
#	debian/no_packlist_perllocal.diff
#	debian/prefix_changes.diff
#	debian/fakeroot.diff
# 	debian/instmodsh_doc.diff
#	debian/ld_run_path.diff
#	debian/libnet_config_path.diff
#	debian/mod_paths.diff
# 	debian/module_build_man_extensions.diff
#	debian/prune_libs.diff
#	debian/fixes/net_smtp_docs.diff
#	debian/perlivp.diff
#	debian/deprecate-with-apt.diff
#	debian/squelch-locale-warnings.diff
#	debian/skip-upstream-git-tests.diff
#	debian/skip-kfreebsd-crash.diff
#	debian/find_html2text.diff
# \
# Source code has been updated already, no need to patch:
#	debian/m68k_thread_stress.diff
#	debian/fixes/extutils-cbuilder-cflags.diff
#	debian/fixes/module-build-home-directory.diff
#	debian/fixes/sys-syslog-socket-timeout-kfreebsd.patch
#	debian/fixes/pod_fixes.diff
#	0001-Fix-misparsing-of-maketext-strings.patch
#	0001-Prevent-premature-hsplit-calls-and-only-trigger-REHA.patch
# \
# Source code has changed, not found content to patch:
#	debian/cpanplus_config_path.diff
#	09_fix_installperl.patch
#	perl-build-in-t-dir.patch
SRC_URI += " \
file://debian/arm_thread_stress_timeout.diff \
file://debian/libperl_embed_doc.diff \
file://debian/disable-zlib-bundling.diff \
file://debian/cpanplus_definstalldirs.diff \
file://debian/fixes/document_makemaker_ccflags.diff \
\
file://Makefile.patch \
file://Makefile.SH_5.20.2.patch \
file://installperl.patch \
file://perl-dynloader.patch \
file://perl-moreconfig.patch \
file://letgcc-find-errno.patch \
file://generate-sh.patch \
file://native-perlinc.patch \
file://perl-enable-gdbm.patch \
file://cross-generate_uudmap.patch \
file://fix_bad_rpath.patch \
file://perl-archlib-exp.patch \
file://dynaloaderhack.patch \
\
file://config.sh \
file://config.sh-32 \
file://config.sh-32-le \
file://config.sh-32-be \
file://config.sh-64 \
file://config.sh-64-le \
file://config.sh-64-be \
file://perl-5.14.3-fix-CVE-2010-4777.patch \
file://0001-Makefile.SH-fix-do_install-failed.patch \
"

CFLAGS += "-I${STAGING_LIBDIR_NATIVE}/perl-native/perl/${PV}/CORE"

# cacheout.pl does not exist in perl-5.20,
# so temporary create it for passing perl_package_preprocess.
# It will be removed after.
perl_package_preprocess_prepend(){
	touch ${PKGD}${libdir}/perl/${PV}/cacheout.pl
}

perl_package_preprocess_append(){
	if [ -f ${PKGD}${libdir}/perl/${PV}/cacheout.pl ]; then
		rm ${PKGD}${libdir}/perl/${PV}/cacheout.pl
	fi
}

# FIXME: temporally fix run-time dependencies of perl modules
# Currently, this recipe is based on perl-rdepends_5.14.3.inc,
# which includes packages that are not provided in Debian:
#   ${PN}-module-list-util-pp, ${PN}-module-scalar-util-pp
# So remove these packages from the dependency chain.
# perl_debian.bb should be re-created from scratch without the base recipe.
RDEPENDS_${PN}-module-list-util = " \
${PN}-module-dynaloader \
${PN}-module-exporter \
${PN}-module-strict \
${PN}-module-vars \
${PN}-module-xsloader \
"
RDEPENDS_${PN}-module-scalar-util = " \
${PN}-module-carp \
${PN}-module-exporter \
${PN}-module-list-util \
${PN}-module-strict \
${PN}-module-vars \
"
