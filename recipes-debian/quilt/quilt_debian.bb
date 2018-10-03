#
# base recipe: recipes-devtools/quilt/quilt_0.65.bb
# base branch: master
# base commit: a5d1288804e517dee113cb9302149541f825d316
#

require quilt.inc

inherit gettext

SRC_URI += "file://gnu_patch_test_fix_target.patch"

EXTRA_AUTORECONF += "--exclude=aclocal"

RDEPENDS_${PN} += "patch diffstat bzip2 util-linux"
