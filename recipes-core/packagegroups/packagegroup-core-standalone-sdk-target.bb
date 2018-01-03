SUMMARY = "Target packages for the standalone SDK"
PR = "r8"

# Prevent apt-get requests packages in wrong architecture
# when enable multilib
PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

RDEPENDS_${PN} = "\
    libgcc \
    libgcc-dev \
    libatomic \
    libatomic-dev \
    libstdc++ \
    libstdc++-dev \
    ${LIBC_DEPENDENCIES} \
    "

RRECOMMENDS_${PN} = "\
    libssp \
    libssp-dev \
    "
