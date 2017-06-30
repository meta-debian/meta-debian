# use simple kernel recipes instead of linux-yocto
PREFERRED_PROVIDER_virtual/kernel = "linux-base"
PREFERRED_PROVIDER_linux-libc-headers = "linux-libc-headers-base"
PREFERRED_PROVIDER_nativesdk-linux-libc-headers = "nativesdk-linux-libc-headers-base"

# no independent udev source in the recent Debian
PREFERRED_PROVIDER_udev = "systemd"

# don't use remake
PREFERRED_PROVIDER_make-native = "make-native"

# use dpkg as update-alternatives instead of opkg-utils
PREFERRED_PROVIDER_virtual/update-alternatives = "dpkg-utils"
PREFERRED_PROVIDER_virtual/update-alternatives-native = "dpkg-utils-native"
PREFERRED_PROVIDER_virtual/nativesdk-update-alternatives = "nativesdk-dpkg-utils"

# Use libusb instead of libusb-compat
PREFERRED_PROVIDER_libusb = "libusb"

# Use xz-utils instead of xz
PREFERRED_PROVIDER_xz = "xz-utils"
PREFERRED_PROVIDER_xz-native = "xz-utils-native"
PREFERRED_PROVIDER_nativesdk-xz = "nativesdk-xz-utils"

# Use six recipes in meta-debian to provide python-six,
# instead of using python-six from poky
PREFERRED_PROVIDER_python-six = "six"

# using recipes libsndfile in meta-debian, 
# instead of libsndfile1 from poky
PREFERRED_PROVIDER_libsndfile1-dev = "libsndfile"
PREFERRED_PROVIDER_libsndfile1 = "libsndfile"
