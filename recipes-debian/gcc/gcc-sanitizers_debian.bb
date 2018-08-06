#
# base recipe: meta/recipes-devtools/gcc/gcc-sanitizers_8.2.bb
# base branch: master
# base commit: da24071e92071ecbefe51314d82bf40f85172485
#

require gcc-8.inc
require recipes-devtools/gcc/gcc-sanitizers.inc

# Building with thumb enabled on armv4t armv5t fails with
# sanitizer_linux.s:5749: Error: lo register required -- `ldr ip,[sp],#8'
ARM_INSTRUCTION_SET_armv4 = "arm"
ARM_INSTRUCTION_SET_armv5 = "arm"
