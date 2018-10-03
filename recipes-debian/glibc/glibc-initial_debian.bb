#
# base recipe: meta/recipes-core/glibc/glibc-initial_2.27.bb
# base branch: master
# base commit: a5d1288804e517dee113cb9302149541f825d316
#

require glibc_${PV}.bb
require recipes-core/glibc/glibc-initial.inc

# main glibc recipes muck with TARGET_CPPFLAGS to point into
# final target sysroot but we
# are not there when building glibc-initial
# so reset it here

TARGET_CPPFLAGS = ""
