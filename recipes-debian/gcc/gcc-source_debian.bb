require gcc-8.inc
require recipes-devtools/gcc/gcc-source.inc

EXCLUDE_FROM_WORLD = "1"

do_unpack[depends] += "xz-native:do_populate_sysroot"
do_unpack_append() {
    bb.build.exec_func('unpack_extra', d)
}

unpack_extra() {
	rm -rf ${S}/*
	tar xf ${DEBIAN_UNPACK_DIR}/gcc-${PV}-dfsg.tar.xz -C ${DEBIAN_UNPACK_DIR}
	mv ${DEBIAN_UNPACK_DIR}/gcc-${PV}/* ${S}/
	rm -rf ${DEBIAN_UNPACK_DIR}/gcc-${PV}
}

# Generate debian/patches/series
do_debian_patch_prepend() {
	# Remove old series file if existed
	test -f ${DEBIAN_QUILT_PATCHES}/series && rm -f ${DEBIAN_QUILT_PATCHES}/series

	debian_patches="$debian_patches \
		gcc-gfdl-build \
		gcc-textdomain \
		gcc-driver-extra-langs \
		gcc-hash-style-gnu \
		libstdc++-pic \
		libstdc++-doclink \
		libstdc++-man-3cxx \
		libstdc++-test-installed \
		alpha-no-ev4-directive \
		note-gnu-stack \
		libgomp-omp_h-multilib \
		pr47818 \
		libgo-testsuite \
		libgo-cleanfiles \
		gcc-target-include-asm \
		libgo-revert-timeout-exp \
		libgo-setcontext-config \
		gcc-auto-build \
		kfreebsd-unwind \
		libitm-no-fortify-source \
		sparc64-biarch-long-double-128 \
		pr66368 \
		pr67590 \
		libjit-ldflags \
		libffi-pax \
		libffi-race-condition \
		gcc-foffload-default \
		gcc-fuse-ld-lld \
		cuda-float128 \
		libffi-mipsen-r6 \
		t-libunwind-elf-Wl-z-defs \
		gcc-alpha-bs-ignore \
		libffi-riscv \
		gcc-force-cross-layout \
		gcc-search-prefixed-as-ld \
		kfreebsd-decimal-float \
		go-vet-tool \
		gcc-as-needed-push-pop \
		ada-arm \
		sys-auxv-header \
		gcc-ice-dump \
		gcc-ice-apport \
		skip-bootstrap-multilib \
		libffi-ro-eh_frame_sect \
		libffi-mips \
		ada-kfreebsd \
		ada-drop-termio-h \
		libgomp-kfreebsd-testsuite \
		go-testsuite \
		ada-749574 \
		ada-changes-in-autogen-output \
		"

	for patch in $debian_patches; do
		echo "$patch".diff >> ${DEBIAN_QUILT_PATCHES}/series
	done
}
