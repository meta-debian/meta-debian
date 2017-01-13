#
# base recipe: http://cgit.openembedded.org/cgit.cgi/meta-openembedded/tree/meta-networking/recipes-support/ipvsadm/ipvsadm_1.26.bb?h=master
# base branch: master
#
SUMMARY = "Linux Virtual Server administration Utility"
DESCRIPTION = "Ipvsadm  is  used  to set up, maintain or inspect the virtual server \
table in the Linux kernel. The Linux Virtual  Server  can  be  used  to \
build  scalable  network  services  based  on  a cluster of two or more nodes. \
The active node of the cluster redirects service requests  to  a \
collection  of  server  hosts  that will actually perform the services. \
Supported features include two protocols (TCP and UDP),  three  packet-forwarding \
methods (NAT, tunneling, and direct routing), and eight load balancing algorithms \
(round robin, weighted round robin,  least-connec-tion, weighted least-connection, \
locality-based  least-connection, locality-based least-connection with replication, \
destination-hashing, and source-hashing)."
SECTION = "net"

inherit debian-package
PV = "1.26"
PR = "r0"

LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://README;beginline=40;endline=56;md5=a54cba37b64924aa5008881607942892"

DEPENDS += "libnl popt"

# 0001-Modify-the-Makefile-for-cross-compile.patch: Modify the Makefile for cross compile 
# 0002-Replace-nl_handle-to-nl_sock.patch: The nl_handle was replace with nl_sock in the libnl-3
SRC_URI += " \
	file://0001-Modify-the-Makefile-for-cross-compile.patch \
	file://0002-Replace-nl_handle-to-nl_sock.patch \
    "

do_compile() {
	oe_runmake \
	CC="${CC} -I${STAGING_INCDIR} -I${STAGING_INCDIR}/libnl3 -L${STAGING_LIBDIR}" all
}

do_install() {
	oe_runmake 'BUILD_ROOT=${D}' install
	
	install -d ${D}${sysconfdir}/default
	install -m 0644 ${S}/debian/ipvsadm.default ${D}${sysconfdir}/default/ipvsadm 
}
