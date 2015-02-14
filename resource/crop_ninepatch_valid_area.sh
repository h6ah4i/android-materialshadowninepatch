#!/bin/bash -e
#
#    Copyright (C) 2015 Haruki Hasegawa
#
#    Licensed under the Apache License, Version 2.0 (the "License");
#    you may not use this file except in compliance with the License.
#    You may obtain a copy of the License at
#
#        http://www.apache.org/licenses/LICENSE-2.0
#
#    Unless required by applicable law or agreed to in writing, software
#    distributed under the License is distributed on an "AS IS" BASIS,
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#    See the License for the specific language governing permissions and
#    limitations under the License.
#

src_file=$1
dest_file=$2
crop_pixels=$3

src_width=$(identify -format "%[fx:w]" $src_file)
src_height=$(identify -format "%[fx:h]" $src_file)

dest_width=$(( src_width - 2 * crop_pixels ))
dest_height=$(( src_height - 2 * crop_pixels ))

convert $src_file -crop $dest_width"x"$dest_height"+"$crop_pixels"+"$crop_pixels $dest_file