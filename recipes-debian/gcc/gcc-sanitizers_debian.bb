#
# base recipe: meta/recipes-devtools/gcc/gcc-sanitizers_8.1.bb
# base branch: master
# base commit: a5d1288804e517dee113cb9302149541f825d316
#

require gcc-8.1.inc
require recipes-devtools/gcc/gcc-sanitizers.inc

# Building with thumb enabled on armv4t armv5t fails with
# sanitizer_linux.s:5749: Error: lo register required -- `ldr ip,[sp],#8'
ARM_INSTRUCTION_SET_armv4 = "arm"
ARM_INSTRUCTION_SET_armv5 = "arm"
