PR = "r0"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

# It seems that PV in OE-Core layer is "1.0".
# This intends make higher priority than the one in OE-Core.
PV = "1.1"

# Almost same definitions as kernelsrc.bbclass,
# but don't want to inherit linux-kernel-base.bbclass
S = "${STAGING_KERNEL_DIR}"
do_fetch[noexec] = "1"
do_unpack[depends] += "virtual/kernel:do_patch"
do_unpack[noexec] = "1"
do_patch[noexec] = "1"
do_package[depends] += "virtual/kernel:do_populate_sysroot"

B = "${WORKDIR}/build"

do_configure[depends] += "virtual/kernel:do_shared_workdir"

# Now configured to provide minimum features
EXTRA_OEMAKE = '\
	-C ${S}/tools/perf \
	O=${B} \
	CROSS_COMPILE=${TARGET_PREFIX} \
	ARCH=${ARCH} \
	CC="${CC}" \
	AR="${AR}" \
	LD="${LD}" \
	DESTDIR=${D} \
	NO_GTK2=1 \
	NO_LIBPERL=1 \
	NO_LIBPYTHON=1 \
	NO_NEWT=1 \
	NO_LIBUNWIND=1 NO_LIBDW_DWARF_UNWIND=1 \
	NO_LIBNUMA=1 \
	NO_SDT=1 \
	V=1 \
'

do_configure() {
	:
}

do_compile() {
	unset CFLAGS
	oe_runmake all
}

do_install() {
	unset CFLAGS
	oe_runmake install
}

PACKAGE_ARCH = "${MACHINE_ARCH}"

RDEPENDS_${PN} += "bash"

FILES_${PN} += "${exec_prefix}/libexec/perf-core ${libdir}/traceevent"
