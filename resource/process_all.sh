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

num_amb_spot_drawables=18
num_composite_drawables=9
res_prefix=ms9_  # material shadow ninepatch

CUR_DIR=$(cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd)

# PHASE 1 : export PNG files
./phase1_batch_inkscape_export.sh $CUR_DIR/material_shadow.svg $CUR_DIR/phase1 $num_amb_spot_drawables $num_composite_drawables $res_prefix

# PHASE 2 : crop
./phase2_batch_crop_ninepatch_valid_area.sh phase1 phase2 $num_amb_spot_drawables $num_composite_drawables $res_prefix

# PHASE 3 : shrink PNG file size
./phase3_batch_shrink_png_file.sh phase2 phase3 $num_amb_spot_drawables $num_composite_drawables $res_prefix

# PHASE 4 : generate alias XML files
./phase4_batch_gen_alias_xml.sh phase3 phase4 $num_amb_spot_drawables $num_composite_drawables $res_prefix