inherit kernel

die() {
	echo "$1"
	exit 1
}

do_defconfig[dirs] = "${WORKDIR}"
do_defconfig() {
	# use LINUX_CONF as defconfig
	rm -f ${S}/defconfig
	[ -n "${LINUX_CONF}" ] || die "ERROR: LINUX_CONF is null, please fix"
	if echo "${LINUX_CONF}" | grep -q "^/"; then
		# absolute path
		DEFCONFIG="${LINUX_CONF}"
	else
		# relative path from ${S}
		DEFCONFIG="${S}/${LINUX_CONF}"
	fi
	[ -f $DEFCONFIG ] || die "ERROR: $DEFCONFIG not found"
	echo "NOTE: use $DEFCONFIG as ${S}/defconfig"
	cp $DEFCONFIG ${S}/defconfig

	# clean & make defconfig
	rm -rf ${B}
	mkdir -p ${B}
	cd ${B}
	# To make source tree clean
	oe_runmake ARCH=${ARCH} -C ${S} mrproper
	oe_runmake \
		ARCH=${ARCH} CROSS_COMPILE=${TARGET_PREFIX} O=${B} -C ${S} \
		KBUILD_DEFCONFIG=../../../defconfig defconfig || \
		die "ERROR: make defconfig failed"
}
addtask defconfig after do_patch before do_configure
