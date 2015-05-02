require recipes-devtools/python/python-native_2.7.3.bb
FILESPATH_prepend = "${COREBASE}/meta/recipes-devtools/python/python-native:"

require python.inc

inherit debian-package

DPR = "0"

# Source code has been updated, no need to patch:
#	nohostlibs.patch (some hunk is not updated, 
#		so use nohostlibs_2.7.8.patch for non-update content)
#	builddir.patch (some hunk is not updated, 
#		so use builddir_2.7.8.patch for non-update content)
#	python-fix-build-error-with-Readline-6.3.patch
#	gcc-4.8-fix-configure-Wformat.patch
# \
# Source code has been changed, not found content to patch:
#	05-enable-ctypes-cross-build.patch
#	multilib.patch (file not found to patch, so update the patch for right file
#			and rename to multilib_2.7.8.patch)
# makerace.patch: copied from python3 directory
#   Always builds 'Parser/pgen' required by do_install
SRC_URI += "\
file://06-ctypes-libffi-fix-configure.patch \
file://10-distutils-fix-swig-parameter.patch \
file://11-distutils-never-modify-shebang-line.patch \
file://12-distutils-prefix-is-inside-staging-area.patch \
file://debug.patch \
file://unixccompiler.patch \
file://nohostlibs_2.7.8.patch \
file://multilib_2.7.8.patch \
file://add-md5module-support.patch \
file://builddir_2.7.8.patch \
file://parallel-makeinst-create-bindir.patch \
file://makerace.patch \
"

# Parallel make causes strange compile error so temporarily disable it
PARALLEL_MAKE = ""
