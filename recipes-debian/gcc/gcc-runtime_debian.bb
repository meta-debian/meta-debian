#
# base recipe: meta/recipes-devtools/gcc/gcc-runtime_8.2.bb
# base branch: master
# base commit: da24071e92071ecbefe51314d82bf40f85172485
#

require gcc-8.inc
require recipes-devtools/gcc/gcc-runtime.inc

# Disable ifuncs for libatomic on arm conflicts -march/-mcpu
EXTRA_OECONF_append_arm = " libat_cv_have_ifunc=no "

FILES_libgomp-dev += "\
    ${libdir}/gcc/${TARGET_SYS}/${BINV}/include/openacc.h \
"
