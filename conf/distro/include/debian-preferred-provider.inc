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

# Use libjpeg-turbo instead of jpeg
PREFERRED_PROVIDER_jpeg = "libjpeg-turbo"
PREFERRED_PROVIDER_jpeg-native = "libjpeg-turbo-native"

# Use libusb instead of libusb-compat
PREFERRED_PROVIDER_libusb = "libusb"
