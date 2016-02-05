#
# Generate debian/patches/series follow debian/rules.patch
#
# DPKG_ARCH is a mapping from TARGET_ARCH to Debian's architectures,
# we use DPKG_ARCH for checking to match with debian/rules.patch.
do_debian_patch_prepend() {
	# Remove old series file if existed
	if [ -f ${DEBIAN_QUILT_PATCHES}/series ]; then
		rm ${DEBIAN_QUILT_PATCHES}/series
	fi

	debian_patches="svn-updates"
	if [ "${DPKG_ARCH}" = "arm64" ]; then
		debian_patches="$debian_patches \
			gcc-linaro \
			gcc-linaro-no-macros \
			linaro331"
	fi

	debian_patches="$debian_patches \
		gcc-gfdl-build \
		gcc-textdomain \
		gcc-driver-extra-langs \
		gcc-hash-style-gnu \
		libstdc++-pic \
		libstdc++-doclink \
		libstdc++-man-3cxx \
		libstdc++-test-installed \
		libjava-stacktrace \
		libjava-jnipath \
		libjava-sjlj \
		libjava-disable-plugin \
		alpha-no-ev4-directive \
		boehm-gc-getnprocs \
		note-gnu-stack \
		libgomp-omp_h-multilib \
		sparc-force-cpu \
		gccgo-version \
		pr47818 \
		pr49944 \
		gcc-base-version \
		libgo-testsuite \
		gcc-target-include-asm \
		libgo-revert-timeout-exp \
		libgo-setcontext-config \
		gcc-auto-build \
		kfreebsd-unwind \
		kfreebsd-boehm-gc \
		hurd-boehm-gc \
		libffi-m68k \
		libitm-no-fortify-source \
		pr57653 \
		pr61257 \
		pr61046 \
		pr61336 \
		pr61126 \
		gcc-setmultilib-fix \
		sparc64-biarch-long-double-128 \
		pr61841 \
		pr61294 \
		pr59586 \
		pr60655-debug-loc \
		pr63751 \
		gccgo-arm64 \
		ada-arm \
		ada-hurd \
		ada-kfreebsd \
		ada-revert-pr63225"

	if [ "${DPKG_ARCH}" = "arm" ] || \
	   [ "${DPKG_ARCH}" = "armel" ] || \
	   [ "${DPKG_ARCH}" = "armhf" ]; then
		debian_patches="$debian_patches libjava-armel-unwind"
	fi
	if [ "${DPKG_ARCH}" = "armhf" ]; then
		debian_patches="$debian_patches gcj-arm-mode"
	fi

	if [ "${DPKG_ARCH}" = "m68k" ]; then
		debian_patches="$debian_patches \
			m68k-ada \
			m68k-revert-pr45144 \
			pr52306"
	fi

	if [ "${DPKG_ARCH}" = "powerpcspe" ]; then
		debian_patches="$debian_patches \
			powerpc_remove_many \
			powerpc_nofprs \
			fix-powerpcspe"
	fi

	debian_patches="$debian_patches \
		gcc-sysroot \
		sys-auxv-header \
		libcilkrts-targets \
		go-use-gold \
		arm-multilib-defaults \
		gcc-ice-hack \
		gcc-ice-apport \
		libjava-fixed-symlinks \
		libffi-ro-eh_frame_sect \
		libjava-nobiarch-check \
		libgomp-kfreebsd-testsuite \
		go-testsuite \
		fix-ffi_call_VFP-with-no-VFP-argument \
		ada-749574 \
		ada-mips \
		ada-ppc64"

	for patch in $debian_patches; do
		echo "$patch".diff >> ${DEBIAN_QUILT_PATCHES}/series
	done
}
