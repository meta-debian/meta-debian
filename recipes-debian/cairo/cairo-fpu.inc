#
# Base on meta/recipes-graphics/cairo/cairo-fpu.inc
#

def get_cairo_fpu_setting(bb, d):
    if d.getVar('TARGET_FPU', True) in [ 'soft' ]:
        return "--disable-some-floating-point"
    return ""

