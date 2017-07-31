SUMMARY = "OpenGL function pointer management library"
DESCRIPTION = "It hides the complexity of dlopen(), dlsym(), glXGetProcAddress(), \
eglGetProcAddress(), etc. from the app developer, with very little \
knowledge needed on their part.  They get to read GL specs and write \
code using undecorated function names like glCompileShader()."
HOMEPAGE = "https://github.com/anholt/libepoxy"

inherit debian-package
PV = "1.2"

# License is Expat but no generic license file exists for it
LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=58ef4c80d401e07bd9ee8b6b58cf464b"

# debian/source/format is "3.0 (quilt)" but there is no debian/patches
DEBIAN_QUILT_PATCHES = ""

DEPENDS = "util-macros virtual/egl virtual/libx11"

inherit autotools pkgconfig distro_features_check pythonnative

# depends on virtual/egl and virtual/libx11
REQUIRED_DISTRO_FEATURES = "opengl x11"
