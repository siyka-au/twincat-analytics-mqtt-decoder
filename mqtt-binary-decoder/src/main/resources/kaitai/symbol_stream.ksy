meta:
  id: twincat_iot_symbol_stream
  endian: le
  bit-endian: le
seq:
  - id: header
    type: stream_header
  - id: symbols
    size: header.len_symbols
    type: symbols(header.num_symbols)
  - id: data_types
    size: header.len_data_types
    type: data_types(header.num_data_types)

types:
  # Stream
  stream_header:
    seq:
      - id: major_version
        type: u1
      - id: minor_version
        type: u1
      - id: len_header
        type: u2
      - id: num_symbols
        type: u4
      - id: len_symbols
        type: u4
      - id: num_data_types
        type: u4
      - id: len_data_types
        type: u4
      - id: used_dynamic_symbols
        type: u4
      - id: code_page
        type: u4
      - id: flags
        type: stream_flags
      - id: reserved1
        type: u4
      - id: reserved2
        type: u4
      - id: reserved3
        type: u4
      - id: reserved4
        type: u4
      - id: hash
        type: guid

  stream_flags:
    seq:
      - id: is_online_change
        type: b1
      - id: is_target_64_bit
        type: b1
      - id: are_base_types_included
        type: b1
      - id: perform_q_sort
        type: b1
      - id: unknown_values
        type: b28
        doc: Needs further decoding work

  # Symbols
  symbols:
    params:
      - id: num_symbols
        type: u4
    seq:
      - id: symbols
        type: symbol
        repeat: expr
        repeat-expr: num_symbols

  symbol:
    seq:
      - id: len
        type: u4
      - id: body
        size: len - 4
        type: symbol_body

  symbol_body:
    seq:
      - id: index_group
        type: u4
      - id: index_offset
        type: u4
      - id: len
        type: u4
      - id: data_type
        type: u4
        enum: ads_data_type
      - id: symbol_flags
        type: symbol_flags
      - id: len_name
        type: u2
      - id: len_type_name
        type: u2
      - id: len_comment
        type: u2
      - id: name
        type: strz
        size: len_name + 1
        encoding: UTF-8
      - id: type_name
        size: len_type_name + 1
        type: strz
        encoding: UTF-8
      - id: comment
        size: len_comment + 1
        type: strz
        encoding: UTF-8
      - id: type_guid
        type: guid

  symbol_flags:
    seq:
      - id: is_persistent
        type: b1
      - id: is_bit_value
        type: b1
      - id: is_reference_to
        type: b1
      - id: has_type_guid
        type: b1
      - id: is_twincat_com_interface_pointer
        type: b1
      - id: is_read_only
        type: b1
      - id: is_interface_method_access
        type: b1
      - id: is_method_deref
        type: b1
      - id: context_mask
        type: b4
      - id: has_attributes
        type: b1
      - id: is_static
        type: b1
      - id: is_initialised_on_reset
        type: b1
      - id: has_extended_flags
        type: b1

  # Data Types
  data_types:
    params:
      - id: num_data_types
        type: u4
    seq:
      - id: data_types
        type: data_type
        repeat: expr
        repeat-expr: num_data_types

  data_type:
    seq:
      - id: len
        type: u4
      - id: body
        size: len - 4
        type: data_type_body

  data_type_body:
    meta:
      xref:
        assembly: TwinCAT.Ads.dll
        class: TwinCAT.Ads.Internal.TcAdsDataType
    seq:
      - id: version
        type: u4
        
      - id: hash_value
        type: u4
        doc: Don't know what this is
        
      - id: type_hash_value
        type: u4
        doc: Don't know what this is
        
      - id: len_data_type
        type: u4
        
      - id: offset
        type: u4
        
      - id: base_data_type
        type: u4
        enum: ads_data_type
        
      - id: flags
        type: data_type_flags
        
      - id: len_name
        type: u2
        
      - id: len_type_name
        type: u2
        
      - id: len_comment
        type: u2
        
      - id: num_array_dimensions
        type: u2
        
      - id: num_sub_items
        type: u2
        
      - id: name
        size: len_name + 1
        type: strz
        encoding: UTF-8
        
      - id: type_name
        size: len_type_name + 1
        type: strz
        encoding: UTF-8
        
      - id: comment
        size: len_comment + 1
        type: strz
        encoding: UTF-8
        
      - id: array_information
        type: array_information
        if: num_array_dimensions > 0
        
      - id: sub_items
        type: data_types(num_sub_items)
        if: num_sub_items > 0

      - id: guid
        type: guid
        if: flags.has_type_guid == true
        
      - id: copy_mask
        type: u8
        repeat: expr
        repeat-expr: len_data_type
        if: flags.has_copy_mask
        
      - id: methods
        type: methods
        if: flags.has_method_infos
        
      - id: attributes
        type: attributes
        if: flags.has_attributes
        
      - id: enums
        type: enums(len_data_type)
        if: flags.has_enum_infos
        
    instances:
      is_array:
        value: num_array_dimensions > 0
      is_struct:
        value: num_sub_items > 0
      is_enum:
        value: flags.has_enum_infos
      

  data_type_flags:
    meta:
      xref:
        assembly: TwinCAT.Ads.dll
        class: TwinCAT.Ads.Internal.AdsDataTypeFlags
    seq:
    - id: is_data_type
      type: b1
    - id: is_data_item
      type: b1
    - id: is_reference_to
      type: b1
    - id: is_method_deref
      type: b1
    - id: is_oversampling_array
      type: b1
    - id: is_bit_value
      type: b1
    - id: is_property_item
      type: b1
    - id: has_type_guid
      type: b1
    - id: is_persistent
      type: b1
    - id: has_copy_mask
      type: b1
    - id: is_twincat_com_interface_pointer
      type: b1
    - id: has_method_infos
      type: b1
    - id: has_attributes
      type: b1
    - id: has_enum_infos
      type: b1
    - id: reserved1
      type: b2
    - id: is_byte_aligned
      type: b1
    - id: is_static
      type: b1
    - id: sp_levels
      type: b1
    - id: ignore_persist
      type: b1
    - id: is_any_size_array
      type: b1
    - id: is_persistant_datatype
      type: b1
    - id: is_initialised_on_result
      type: b1
    - id: reserved2
      type: b9

  # Attributes
  attributes:
    seq:
      - id: num_attributes
        type: u2
      - id: attributes
        type: attribute
        repeat: expr
        repeat-expr: num_attributes

  attribute:
    meta:
      xref:
        assembly: TwinCAT.Ads.dll
        class: TwinCAT.Ads.Internal.AdsAttributeEntry
    seq:
      - id: len_key
        type: u1
      - id: len_value
        type: u1
      - id: key
        type: strz
        size: len_key + 1
        encoding: UTF-8
      - id: value
        type: strz
        size: len_value + 1
        encoding: UTF-8

  # Arrays
  array_information:
    meta:
      xref:
        assembly: TwinCAT.Ads.dll
        class: TwinCAT.Ads.Internal.AdsDatatypeArrayInfo
    seq:
      - id: lower_bounds
        type: u4
      - id: num_elements
        type: u4

  # Methods
  methods:
    seq:
      - id: num_methods
        type: u2
      - id: methods
        type: method
        repeat: expr
        repeat-expr: num_methods

  method:
    meta:
      xref:
        assembly: TwinCAT.Ads.dll
        class: TwinCAT.Ads.Internal.AdsMethodEntry
    seq:
      - type: u8 # TODO
      
  method_parameter:
    meta:
      xref:
        assembly: TwinCAT.Ads.dll
        class: TwinCAT.Ads.Internal.AdsMethodParaInfo
    seq:
      - type: u8 # TODO
      
  method_parameter_flag:
    meta:
      xref:
        assembly: TwinCAT.Ads.dll
        class: TwinCAT.TypeSystem.MethodParamFlags
    seq:
      - id: in
        type: b1
      - id: out
        type: b1
      - id: reference
        type: b1
      - id: reserved1
        type: b29

  # Enums
  enums:
    params:
      - id: len
        type: u4
    seq:
      - id: num_enum_items
        type: u2
      - id: enum_items
        type: enum_item(len)
        repeat: expr
        repeat-expr: num_enum_items
        
  enum_item:
    meta:
      xref:
        assembly: TwinCAT.Ads.dll
        class: TwinCAT.Ads.Internal.AdsEnumInfoEntry
    params:
      - id: len
        type: u4
    seq:
      - id: len_name
        type: u1
      - id: name
        type: strz
        size: len_name + 1
        encoding: UTF-8
      - id: value
        type: u1
        repeat: expr
        repeat-expr: len

  # Supporting Types
  guid:
    seq:
      - id: data1
        type: u4
      - id: data2
        type: u2
      - id: data3
        type: u2
      - id: data4
        type: u4be
      - id: data4a
        type: u4be

# Enums
enums:
  ads_data_type:
    0: void
    16: int8
    17: uint8
    2: int16
    18: uint16
    3: int32
    19: uint32
    20: int64
    21: uint64
    4: real32
    5: real64
    65: big_type
    30: string
    31: w_string
    32: read80
    33: bit
    34: max_types

  data_category:
    0: none_or_unknown # Uninitialized / NotProcessed (0)
    1: primitive # Simple / Base Data Type (1)
    2: alias # Alias data type (2)
    3: enum # Enumeration data type (3)
    4: array # Array data type (4)
    5: struct # Structure data type (5)
    6: function_block # Function block (POU) (6)
    7: program # Program (POU) (7)
    8: function # Function (POU) (8)
    9: sub_range # SubRange (9)
    10: string # Fixed length string (10)
    12: bitset # Bitset (12)
    13: pointer # Pointer type (13)
    14: union # Union type (14)
    15: reference # Reference type (15)
    16: interface # The interface

  method_parameter_flag_mask:
    # see method_parameter_flag
    5: mask_in
    6: mask_out
