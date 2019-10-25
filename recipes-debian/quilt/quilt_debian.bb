#
# base recipe: recipes-devtools/quilt/quilt_0.65.bb
# base branch: master
# base commit: 821a6f2a170cfcaf8fe51240a2558ae06328a998
#

require quilt.inc
inherit gettext

RDEPENDS_${PN}-ptest += "${PN}"
