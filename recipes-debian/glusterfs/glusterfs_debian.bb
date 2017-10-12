SUMMARY = "clustered file-system"
DESCRIPTION = "GlusterFS is a clustered file-system capable of scaling to several \
 peta-bytes. It aggregates various storage bricks over Infiniband RDMA \
 or TCP/IP interconnect into one large parallel network file \
 system. GlusterFS is one of the most sophisticated file system in \
 terms of features and extensibility. It borrows a powerful concept \
 called Translators from GNU Hurd kernel. Much of the code in GlusterFS \
 is in userspace and easily manageable."
HOMEPAGE = "http://www.gluster.org/"

inherit debian-package
PV = "3.5.2"

LICENSE = "GPLv2+ | LGPLv3+"
LIC_FILES_CHKSUM = "file://COPYING-GPLV2;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING-LGPLV3;md5=e6a600fd5e1d9cbde2d983680233ad02 \
                    file://contrib/fuse-util/COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

SRC_URI += "file://xlator-host-contamination.patch"
SRC_URI_append_class-target = " file://0001-avoid-using-python-native-by-default.patch"

inherit autotools pythonnative

EXTRA_OECONF += "--libexecdir=${libdir}"
DEPENDS += "flex-native libibverbs librdmacm lvm2 fuse db libaio bison-native \
            readline ncurses glib-2.0 openssl libxml2 flex"

# Don't check the distribution to prevent do_configure failed.
CACHED_CONFIGUREVARS = 'ac_cv_file__etc_debian_version=no \
                        ac_cv_file__etc_SuSE_release=no \
                        ac_cv_file__etc_redhat_release=no \
                        ac_cv_lib_lex=""'

# need to export these variables for python runtime
# fix error:
#       PREFIX = os.path.normpath(sys.prefix).replace( os.getenv("BUILD_SYS"), os.getenv("HOST_SYS") )
#       TypeError: Can't convert 'NoneType' object to str implicitly
export BUILD_SYS
export HOST_SYS

do_install_append() {
	# Follow debian/glusterfs-common.dirs
	install -d ${D}${localstatedir}/log/glusterfs

	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${S}/debian/glusterfs-server.init \
		${D}${sysconfdir}/init.d/glusterfs-server

	install -d ${D}${sysconfdir}/logrotate.d
	install -m 0644 ${S}/debian/glusterfs-common.logrotate \
		${D}${sysconfdir}/logrotate.d/glusterfs-common

	# Base on debian/rules
	mv ${D}${datadir}/glusterfs/scripts/gsync-sync-gfid \
	   ${D}${libdir}/glusterfs/
	find ${D}${libdir} -type f -name \*.la \
	     -exec sed 's/^dependency_libs/#dependency_libs/g' -i {} \;

	chmod +x ${D}${datadir}/glusterfs/scripts/*.sh

	# Remove unwanted files
	find ${D}${libdir} -type f -name "*.pyo" -exec rm -f {} \;
	rm -rf ${D}${localstatedir}/run \
	       ${D}${sysconfdir}/glusterfs/glusterfs-logrotate

	# Base on debian/glusterfs-common.install
	install -m 0644 ${S}/libglusterfs/src/*.h ${D}${includedir}/glusterfs/
	install -m 0644 ${B}/libglusterfs/src/y.tab.h ${D}${includedir}/glusterfs/
	install -d ${D}${datadir}/emacs/site-lisp/
	mv ${D}${docdir}/glusterfs/glusterfs-mode.el \
	   ${D}${datadir}/emacs/site-lisp/

	# Base on glusterfs-common.links
	ln -sf ../../../..${libdir}/glusterfs/gsync-sync-gfid \
		${D}${datadir}/glusterfs/scripts/gsync-sync-gfid

	# Base on debian/glusterfs-server.install
	install -D -m 0755 ${S}/extras/hook-scripts/S56glusterd-geo-rep-create-post.sh \
		${D}${localstatedir}/lib/glusterd/hooks/1/gsync-create/post/S56glusterd-geo-rep-create-post.sh
}

# There are some symlinks to a .so but this is valid.
INSANE_SKIP_${PN}-common = "dev-so"

PACKAGES =+ "${PN}-client ${PN}-common ${PN}-server"

FILES_${PN}-client = "${base_sbindir}/mount.glusterfs \
                      ${bindir}/fusermount-glusterfs \
                      ${sbindir}/glusterfs"
FILES_${PN}-common = "${sysconfdir}/logrotate.d/glusterfs-common \
                      ${includedir}/glusterfs/* \
                      ${libdir}/glusterfs/gsync-sync-gfid \
                      ${libdir}/ocf/resource.d/* \
                      ${libdir}/python* \
                      ${libdir}/glusterfs/${PV}/*/*.so \
                      ${libdir}/glusterfs/${PV}/*/*/*.so \
                      ${libdir}/glusterfs/${PV}/*/*/*/*.so \
                      ${libdir}/glusterfs/${PV}/*/*/*/*.py \
                      ${libdir}/glusterfs/gsyncd \
                      ${libdir}/glusterfs/gverify.sh \
                      ${libdir}/glusterfs/peer_add_secret_pub \
                      ${libdir}/glusterfs/peer_gsec_create \
                      ${libdir}/glusterfs/python/* \
                      ${libdir}/*${SOLIBS} \
                      ${libdir}/pkgconfig/*.pc \
                      ${sbindir}/glusterfsd \
                      ${datadir}/emacs/site-lisp/glusterfs-mode.el \
                      ${localstatedir}/log/glusterfs \
                      ${datadir}/glusterfs/scripts/* \
                      "
FILES_${PN}-server = "${sysconfdir}/glusterfs/* \
                      ${sysconfdir}/init.d/* \
                      ${sbindir}/glfsheal \
                      ${sbindir}/gluster \
                      ${sbindir}/glusterd \
                      ${localstatedir}/lib/glusterd/* \
                      "

RDEPENDS_${PN}-client += "python fuse"
RDEPENDS_${PN}-server += "${PN}-client lsb-base"
RRECOMMENDS_${PN}-server += "nfs-common"
