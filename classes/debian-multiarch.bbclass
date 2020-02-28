#
# debian-multiarch.bbclass
#

# Use TUNE_ARCH instead of TARGET_ARCH because if recipe inherits "allarch"
# TARGET_ARCH will be set to "allarch", so we can't get what DEB_HOST_MULTIARCH we want.
GNU_HOST_SUFFIX = "${@get_gnu_suffix(d.getVar('TUNE_ARCH', True), d.getVar('TUNE_FEATURES', True))}"
DEB_HOST_MULTIARCH = "${@arch_to_multiarch(d.getVar('TUNE_ARCH', True))}-${TARGET_OS}${GNU_HOST_SUFFIX}"

# Help target recipe be able to get location of multiarch libdir in sdk's sysroot.
GNU_SDK_SUFFIX = "${@get_gnu_suffix(d.getVar('SDK_ARCH', True), '')}"
DEB_SDK_MULTIARCH = "${@arch_to_multiarch(d.getVar('SDK_ARCH', True))}-${SDK_OS}${GNU_SDK_SUFFIX}"
DEB_HOST_MULTIARCH_class-nativesdk = "${DEB_SDK_MULTIARCH}"

# Provide an alternative DEB_HOST_MULTIARCH for native environment,
# so target recipe can get native multiarch 'libdir'.
GNU_BUILD_SUFFIX = "${@get_gnu_suffix(d.getVar('BUILD_ARCH', True), '')}"
DEB_BUILD_MULTIARCH = "${@arch_to_multiarch(d.getVar('BUILD_ARCH', True))}-${BUILD_OS}${GNU_BUILD_SUFFIX}"
DEB_HOST_MULTIARCH_class-native = "${DEB_BUILD_MULTIARCH}"

# Additional flags to help native recipes/commands be able to build/run with native libraries.
BUILD_LDFLAGS_MULTIARCH = " \
    -L${STAGING_LIBDIR_NATIVE}/${DEB_HOST_MULTIARCH} \
    -L${STAGING_BASE_LIBDIR_NATIVE}/${DEB_HOST_MULTIARCH} \
    -Wl,-rpath-link,${STAGING_LIBDIR_NATIVE}/${DEB_HOST_MULTIARCH} \
    -Wl,-rpath-link,${STAGING_BASE_LIBDIR_NATIVE}/${DEB_HOST_MULTIARCH} \
    -Wl,-rpath,${STAGING_LIBDIR_NATIVE}/${DEB_HOST_MULTIARCH} \
    -Wl,-rpath,${STAGING_BASE_LIBDIR_NATIVE}/${DEB_HOST_MULTIARCH} \
"
BUILD_LDFLAGS .= "${BUILD_LDFLAGS_MULTIARCH}"

# libdir will be change automatically to multiarch libdir.
# Set this variable to 1 in recipe if you want to handle libdir by yourself.
#   1: baselib = lib
#   0: baselib = lib/${DEB_HOST_MULTIARCH}
KEEP_NONARCH_BASELIB ?= "0"

# NOTE: native, nativesdk and crosssdk define baselib to 'lib' in their class
#       where we can't overwrite.
baselib = "${@base_conditional('KEEP_NONARCH_BASELIB','0','lib/${DEB_HOST_MULTIARCH}','lib',d)}"

PKG_CONFIG_PATH_APPEND = "${@base_conditional('KEEP_NONARCH_BASELIB','0',\
':${STAGING_EXECPREFIXDIR}/lib/pkgconfig',\
':${STAGING_LIBDIR}/${DEB_HOST_MULTIARCH}/pkgconfig',\
d)}"
PKG_CONFIG_PATH_APPEND_class-native = ":${STAGING_LIBDIR}/${DEB_HOST_MULTIARCH}/pkgconfig"
PKG_CONFIG_PATH_APPEND_class-nativesdk = ":${STAGING_LIBDIR}/${DEB_HOST_MULTIARCH}/pkgconfig"
PKG_CONFIG_PATH_APPEND_class-crosssdk = ":${STAGING_LIBDIR}/${DEB_HOST_MULTIARCH}/pkgconfig"
PKG_CONFIG_PATH .= "${PKG_CONFIG_PATH_APPEND}"

# in case libdir is /usr/lib/<triplet>, python and perl modules
# will be missing in sysroot destdir
SYSROOT_DIRS += " \
    ${nonarch_libdir}/python* \
    ${nonarch_libdir}/perl \
"

def arch_to_multiarch(arch):
    import re
    if re.match(r'i[4567]86',arch):
        return "i386"
    else:
        return arch


def get_gnu_suffix(arch, tune):
    tune_features = tune.split()
    # If arch is arm, TARGET_OS already contains "-gnueabi" itself
    if arch.startswith("arm"):
        # If arch is armel, suffix will be "-gnueabi"
        # if armhf, suffix will be "-gnueabihf"
        return ['', 'hf']['callconvention-hard' in tune_features]
    else:
        return '-gnu'


# Add "Multi-Arch: " information to metadata of .deb file.
# This helps apt-get manage and install multiarch packages.
# https://wiki.ubuntu.com/MultiarchSpec
DEBIAN_CONTROL ?= "${DEBIAN_UNPACK_DIR}/debian/control"
def deb_ctrl_multi_arch(d):
    def add_multi_arch_metadata(package, multi_arch):
        metadata = d.getVar("PACKAGE_ADD_METADATA_DEB_" + package, True)
        if not metadata:
            metadata = ""
        metadata = metadata.rstrip() + "\n" + "Multi-Arch: " + multi_arch
        d.setVar("PACKAGE_ADD_METADATA_DEB_" + package, metadata)


    # if PACKAGE_ARCH is 'all', do_package_deb will auto write "Multi-Arch: foreign"
    # to control file. We don't want it to be duplicated.
    if d.getVar('PACKAGE_ARCH', True) == "all":
        return 0

    deb_ctrl = d.getVar('DEBIAN_CONTROL', True)
    multi_arch_map = {}
    if os.path.isfile(deb_ctrl):
        with open(deb_ctrl, 'r') as f:
            import re

            package = ""
            mlprefix = d.getVar('MLPREFIX', True)

            package_pattern = "^Package:\s*\S+"
            multi_arch_pattern = "^Multi-Arch:\s*\S+"

            for line in f:
                line = line.rstrip()

                if re.match(package_pattern, line):
                    package = re.split("^Package:\s*", line)[1]
                    if mlprefix:
                        package = mlprefix + package
                elif re.match(multi_arch_pattern, line) and package != "":
                    multi_arch = re.split("^Multi-Arch:\s*", line)[1]
                    multi_arch_map[package] = multi_arch

    for package in (d.getVar('PACKAGES', True) or "").split():
        pkg_pn = d.getVar('PKG_' + package, True)

        # MULTI_ARCH_pkg is set in recipe will have higher priority
        # than in debian/control
        multi_arch = d.getVar('MULTI_ARCH_' + package, True)
        if multi_arch:
            multi_arch_map[package] = multi_arch
            multi_arch_map[pkg_pn] = multi_arch

        if multi_arch_map.get(package):
            add_multi_arch_metadata(package, multi_arch_map[package])

        if package != pkg_pn and multi_arch_map.get(pkg_pn):
            add_multi_arch_metadata(package, multi_arch_map[pkg_pn])

python do_package_deb_prepend() {
    deb_ctrl_multi_arch(d)
}


# 'apt' treats 'pkgA' and 'lib32-pkgA' (or lib64-pkgA) as 2 different packages
# so "Multi-Arch" field has no effect when "apt-get install" on target.
# Make lib32-pkgA also provide pkgA (only in .deb file)
# so 'apt' will treat them as 'pkgA:amd64' and 'pkgA:i386'.
python debian_package_name_hook_append() {
    import re

    mlpre = d.getVar('MLPREFIX', True)
    if mlpre and mlpre != "nativesdk-":
        for pkg in (d.getVar('PACKAGES', True) or "").split():
            # Packages *-dbg, which are made with poky, are not same as *-dbg on Debian.
            # They contains debug files in /bin/, /usr/bin, etc. with no multi-arch specific path.
            # There is no way to install 2 architectures of them in same sdk without let them
            # overwrite files of each other.
            # (like poky still had done with DPkg::Options "--force-all")
            #
            # By default, poky will force install all packages which is listed in
            # packagegroup-core-standalone-sdk-target in both architectures if we enable multilib.
            # This list contains *-dbg packages, too.
            # Making 'lib32-pkgA-dbg' provide 'pkgA-dbg' can break "overwrite" behavior of poky
            # then cause do_populate_sdk fail.
            # Just keep *-dbg packages as before until we have a better solution.
            if pkg.endswith("-dbg"):
                continue

            rprovs = d.getVar("RPROVIDES_%s" % pkg, True) or ""

            if pkg.startswith(mlpre):
                pkg_without_mlpre = re.split("^%s" % mlpre, pkg)[1]
                rprovs += " " + pkg_without_mlpre

            for rprov in rprovs.split():
                if rprov.startswith(mlpre):
                    rprov_without_mlpre = re.split("^%s" % mlpre, rprov)[1]
                    rprovs += " " + rprov_without_mlpre

            d.setVar("RPROVIDES_%s" % pkg, rprovs)
}
