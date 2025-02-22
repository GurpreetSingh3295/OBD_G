/********************************** (C) COPYRIGHT 2024 OBD Experts Ltd. ***************************************************************************
 Odomatic.h  - Contains the function definitions for calling and using Odomatic.
               - Also contains all the definition for the supported data
               NOTE: Some of the definition are potentially still under development so use at own risk if licensed.
  
  This file is not to be shared outside of the licensed organisation. Doing so will result in a breached license condition and action will be taken
***************************************************************************************************************************************************/

#ifndef OdomaticHeader
#define OdomaticHeader

#define ODOMETER                    (1)
#define ALT_ODOMETER                (10)  // To be used in the rare cases where odometer cannot be determined in just one request (NISSAN)
    
#define BC_ODOMETER                 (2)
#define BC_ODOMETER_2               (21)
#define BC_ODOMETER_3               (22)
#define BC_ODOMETER_4               (23)
#define BC_ODOMETER_5               (24)
#define BC_ODOMETER_6               (25)


#define RQ_ODOMETER                 (3)
#define RQ_ODOMETER_2               (31)
#define RQ_ODOMETER_3               (32)
#define RQ_ODOMETER_4               (33)
#define RQ_ODOMETER_5               (34)
#define RQ_ODOMETER_6               (35)


#define RQ_ODOMETER_LA              (40)
#define RQ_ODOMETER_LA_2            (41)

#define BC_SPEED                    (45)    // Broadcast speed


#define BC_FUEL                     (50)
#define RQ_FUEL                     (51)
#define FUEL                        (52)
#define RQ_FUEL_2                   (53)
#define RQ_FUEL_3                   (54)
#define RQ_FUEL_4                   (55)
#define RQ_FUEL_5                   (56)


/**********Tire pressures - Conversion returns data in Kilopascal or kPA */
/**********Tire orientation cannot be guranteed*/
#define RQ_TP1                      (80)    // Front Right
#define RQ_TP2                      (81)    // Front Left
#define RQ_TP4                      (82)    // Rear Right
#define RQ_TP3                      (83)    // Rear Left
#define RQ_TP5                      (84)    // Spare
#define TP_WARNING                  (85)    // Low pressure warning light status
// Start of possible values returned by TP_WARNING
    #define TP_WARN_OFF                 (0)   
    #define TP_WARN_ON                  (1)


#define FRONTLEFT_DOORAJAR          (100)   // Drivers Door open status
#define FRONTRIGHT_DOORAJAR         (101)   // Passengers Door open status
#define REARLEFT_DOORAJAR           (102)   // Rear Left Door open status
#define REARRIGHT_DOORAJAR          (103)   // Rear Right Door open status
#define TRUNK_DOORAJAR              (104)   // Trunk/Boot open status
#define HOOD_DOORAJAR               (105)   // Hood/Bonnet/Frunk open status
#define ALL_DOORAJAR                (106)   // All Hood/Bonnet/Frunk and Doors open status
// Start of possible values returned by DOORAJAR
    #define DOORAJAR_CLOSED                 (0)
    #define DOORAJAR_OPEN                   (1)
    #define FLDOOR_OPEN                     (2)
    #define FRDOOR_OPEN                     (4)
    #define RLDOOR_OPEN                     (8)
    #define RRDOOR_OPEN                     (16)
    #define TRUNK_OPEN                      (32)
    #define HOOD_OPEN                       (64)
    #define DOORAJAR_UNKNOWN                (0x80)
// End of possible values returned by DOOR AJAR


#define GEAR                        (110)   // Gear Selected
// Start of possible values returned by GEAR 
    #define GEAR_PARK                       (0)
    #define GEAR_REVERSE                    (1)
    #define GEAR_NEUTRAL                    (2)
    #define GEAR_DRIVE                      (3)
    #define GEAR_D1                         (4)
    #define GEAR_D2                         (5)
    #define GEAR_D3                         (6)
    #define GEAR_D4                         (7)
    #define GEAR_D5                         (8)
    #define GEAR_D6                         (9)
    #define GEAR_D7                         (10)
    #define GEAR_D8                         (11)
    #define GEAR_D9                         (12)
    #define GEAR_D10                        (13)
    #define GEAR_M1                         (14)
    #define GEAR_M2                         (15)
    #define GEAR_M3                         (16)
    #define GEAR_M4                         (17)
    #define GEAR_M5                         (18)
    #define GEAR_M6                         (19)
    #define GEAR_SPORT                      (20)
    #define GEAR_LOW                        (21)
    #define GEAR_NO_GEAR                    (22)
    #define GEAR_UNKNOWN_GEAR               (23)
    #define GEAR_PARK_NEUTRAL               (24)
    #define GEAR_MANUAL                     (25)
    #define GEAR_BRAKE                      (26)
// End of possible values returned by GEAR 


#define RQ_IS1                      (120)   // Ignition status
// Start of possible values returned by Ignition Status request
    #define IGNITION_LOCK                   (0) // Position 1
    #define IGNITION_OFF                    (1) // Pos 0
    #define IGNITION_ACC_POS                (2) // Pos 2
    #define IGNITION_OFF_ACC_POS            (3)
    #define IGNITION_RUN                    (4) // Pos 2 but engine running
    #define IGNITION_START                  (5)
    #define IGNITION_ON                     (6)  // Could be running,start or acc pos
    #define IGNITION_RUN_ACC_POS            (7)  // Ignition in Acc pos or engine running
    #define IGNITION_ERROR                  (8)  // Ignition problem reported
    #define IGNITION_UNKNOWN                (0xFF)


#define ALL_DOORS_LOCK              (122)   
#define FRONT_DOORS_LOCK            (123)   
#define DRIVERS_FL_DOORLOCK         (124) 
#define PASSENGERS_FR_DOORLOCK      (125)  
#define REAR_DOORS_LOCK             (126)   
#define REARLEFT_DOORLOCK           (127)  
#define REARRIGHT_DOORLOCK          (128)  
// Start of possible values returned for DOOR LOCK
    #define DOORLOCK_UNLOCKED               (0)
    #define DOORLOCKED_DRIVERS              (1)
    #define DOORLOCKED_PASSENGERS           (2)
    #define DOORLOCK_FRONTLOCKED            (3)
    #define DOORLOCKED_LEFT_REAR            (4)
    #define DOORLOCKED_RIGHT_REAR           (8)
    #define DOORLOCK_REARLOCKED             (12)
    #define DOORLOCK_ALLDOORSLOCKED         (15)
    #define DOORLOCKED_BOOT_TRUNK           (16)
    #define DOORLOCKED_HOOD_BONNET          (32)
    #define DOORLOCK_ALLLOCKED              (0x7F)
    #define DOORLOCK_STATUS_UNKNOWN         (0x80)
// NOTE: Multiple door conditions may be returned. e.g. If 3 is returned both drivers and passenger doors are open.
// NOTE: As an example 3 may get return if just requesting DRIVERS_FL_DOORLOCK and the result will need masking with DOORLOCK_DRIVERS to obtain the required information.
// End of possible values returned by DOORLOCK1


#define STATEOFCHARGE               (140)   // State Of Charge %
#define STATEOFHEALTH               (141)   // State Of Health %
#define BAT_CAP_RAW_AH              (142)   // Raw battery capacity supplied often higher than useable value. Provided in Ampere hours(Ah).
#define BAT_CAP_CALC_KWH            (143)   // Calculated battery capacity. Thought to be a more realistic value. Provided in kilowatt hours(kWh).
#define BATTERY                     (145)   // Battery Voltage V
#define HV_BAT_TEMP                 (146)   // Battery Temp C
#define CHARGING_ON_OFF             (147)
#define CHARGING_SOURCE             (148)
    #define CHARGER_OFF                    (0)
    #define CHARGER_AC                     (1)
    #define CHARGER_DC                     (2)
    #define CHARGING                       (3)
    #define CHARGED                        (4)
    #define CHARGER_FAULT                  (5)
    #define CHARGER_WAIT                   (6)
    #define CHARGER_READY                  (7)
    #define NORMAL_CHARGING                (8)
    #define RAPID_CHARGING                 (9)          // AKA Nissan Level3
    #define ONE10V_CHARGING                (10)         // AKA Nissan Level1
    #define TWO40V_CHARGING                (11)         // AKA Nissan Level2
    #define CHARGER_UNKNOWN                (0xFF)

#define YEAR_MAKE_MODEL             (150)

#define ALL_SEATBELTS               (180)
#define FRONTLEFT_SEATBELT          (181)
#define FRONTRIGHT_SEATBELT         (182)
#define FRONTCENTER_SEATBELT        (183)
#define LEFTROW2_SEATBELT           (184)
#define RIGHTROW2_SEATBELT          (185)
#define CENTERROW2_SEATBELT         (186)
#define FRONT_SEATBELTS             (187)
#define ROW2_SEATBELTS              (188)
#define ROW3_SEATBELTS              (189)
#define LEFTROW3_SEATBELT           (190)
#define RIGHTROW3_SEATBELT          (191)
#define CENTERROW3_SEATBELT         (192)
    #define SEATBELT_UNFASTENED             (0)    // Unfastened could also mean unknown
    #define LF_SEATBELT_FASTENED            (1)
    #define RF_SEATBELT_FASTENED            (2)
    #define CF_SEATBELT_FASTENED            (4)
    #define L2_SEATBELT_FASTENED            (8)
    #define R2_SEATBELT_FASTENED            (16)
    #define C2_SEATBELT_FASTENED            (32)
    #define L3_SEATBELT_FASTENED            (64)
    #define R3_SEATBELT_FASTENED            (128)
    #define C3_SEATBELT_FASTENED            (256)
#define FRONTLEFT_SEATBELT_LIGHT    (193)           // If this returns on then this implies the seat is occupied, but the belt not fastened.
#define FRONTRIGHT_SEATBELT_LIGHT   (194)           // If this returns on then this implies the seat is occupied, but the belt not fastened.
    #define SEATBELT_LIGHT_OFF              (0)     // Seatbelt fastened or seat empty
    #define SEATBELT_LIGHT_ON               (1)     // Seat Occupied and seatbelt unfastened

#define ALL_DOORS_LOCK2              (129)   // DEV ONLY
#define RQ_TEST1 (200)
#define RQ_TEST2 (201)
#define RQ_TEST3 (202)
#define RQ_TEST4 (203)
#define RQ_TEST5 (204)
#define RQ_TEST6 (205)
#define RQ_TEST7 (206)
#define RQ_TEST8 (207)
#define RQ_TEST9 (208)
#define RQ_TEST10 (209)
#define RQ_TEST11 (210)
#define RQ_TEST12 (211)
#define RQ_TEST13 (212)
#define RQ_TEST14 (213)
#define RQ_TEST15 (214)

#define ODOMATIC_SUPPORTED1 (253)           // Returns the supported parameter in bytes 1-18 
#define ODOMATIC_SUPPORTED2 (254)
/**********************************************************************************************************/
/*Start Attributes*/

#define FUEL_TYPE                           (1)
    /***** FUEL TYPES*************/
    #define FUEL_UNKNOWN             (0)
    #define FUEL_GAS                 (1)
    #define FUEL_DIESEL              (2)
    #define FUEL_HYBRID              (3)
    #define FUEL_PHEV                (4)
    #define FUEL_ELECTRIC            (5)
    #define FUEL_HYDROGEN            (6)
    #define FUEL_OTHER               (7)

#define BATTERY_CAPACITY_NEW               (2)
#define ATTRIBUTE_UNDEFINED                (0xFF)
/*End Attributes*/

#ifndef OBDEXPERTS_STACK
#define CAN15765_11_500     (1)             /*!<  CAN 11 bit 500Kbps */
#define CAN15765_29_500     (2)             /*!<  CAN 29 bit 500Kbps */
#define CAN_11_500_NISSAN   (20)            /*!<  CAN 11 bit 500Kbps special to read Nissan leaf VIN */
#define CAN_11_500_EXTENDED (21)            /*!<  CAN 11 bit 500Kbps extended address */
#define CAN_3_11_11_250     (22)            /*!<  CAN 11 bit 250Kbps on pins 3 & 11 */
#define CAN_3_11_11_500     (23)            /*!<  CAN 11 bit 500Kbps on pins 3 & 11 */
#endif


#define ODO_PROTOCOL            (0)     // Contains the OBD Experts define for the protocol to connect to for this request/broadcast
#define ODO_ID1                 (1)     // Upper byte of a 29-bit CAN identifier.Contains zero for other protocols.
#define ODO_ID2                 (2)     // Second MSByte for CAN 29-bit identifier.
#define ODO_ID3                 (3)     // Third identifier byte used by 29-bit CAN and uppermost byte used by 11-bit CAN identifier. 
#define ODO_ID4                 (4)     // LSByte used by All protocols
#define ODO_TXLEN               (5)     // Contains a value to indicate the number of bytes to be transmitted or zero to indicate a broadcast message
#define ODO_DATA1_MATCHP1       (6)     // Contains the first byte to be transmitted or if broadcast then could contain the position in the response that must match the value in the next field
#define ODO_DATA2_MATCH1DATA    (7)     // Contains the second byte to be transmitted or if broadcast and ODO_DATA1_MATCHP1 is non-zero then contains the data to be matched
#define ODO_DATA3_MATCHP2       (8)     // Contains the third byte to be transmitted or if broadcast then could contain the position in the response that must match the value in the next field
#define ODO_DATA4_MATCH2DATA    (9)     // Contains the fourth byte to be transmitted or if broadcast and ODO_DATA1_MATCHP2 is non-zero then contains the data to be matched
#define ODO_DATA5               (10)    // Contains the fifth data byte to be transmitted
#define ODO_AR1_EXTEND          (11)    // If non-zero then this field contains the upper byte of the non OBD-II standard CAN identifier response. If this is a broadcast message then a one in this field indicated a longer time needs tobe waited for the broadcast message to respond.
#define ODO_AR2                 (12)    // If non-zero then this field contains the lower byte of the non OBD-II standard CAN identifier response.
#define ODO_PM1                 (13)    // If non-zero this indicates that a pre-message needs to be transmitted prior to getting a response to the main message
#define ODO_LITERS              (14)    // If this field contains a one during a fuel request then this indicated the returned value is in liters and not a percentage of the fuel volume.

#define ODO19_RXLEN             (6)     // Contains the first byte to be transmitted or if broadcast then could contain the position in the response that must match the value in the next field
#define ODO19_RXBYTE1           (7)     // Contains the first byte to be transmitted or if broadcast then could contain the position in the response that must match the value in the next field
#define ODO19_MSBLSB            (8)     // Contains the first byte to be transmitted or if broadcast then could contain the position in the response that must match the value in the next field
#define ODO19_MATH              (9)     // Contains the first byte to be transmitted or if broadcast then could contain the position in the response that must match the value in the next field
#define ODO19_DATA1_MATCHP1     (10)    // Contains the first byte to be transmitted or if broadcast then could contain the position in the response that must match the value in the next field
#define ODO19_DATA2_MATCH1DATA  (11)    // Contains the second byte to be transmitted or if broadcast and ODO_DATA1_MATCHP1 is non-zero then contains the data to be matched
#define ODO19_DATA3_MATCHP2     (12)    // Contains the third byte to be transmitted or if broadcast then could contain the position in the response that must match the value in the next field
#define ODO19_DATA4_MATCH2DATA  (13)    // Contains the fourth byte to be transmitted or if broadcast and ODO_DATA1_MATCHP2 is non-zero then contains the data to be matched
#define ODO19_DATA5             (14)    // Contains the fifth data byte to be transmitted
#define ODO19_AR1_EXTEND        (15)    // If non-zero then this field contains the upper byte of the non OBD-II standard CAN identifier response. If this is a broadcast message then a one in this field indicated a longer time needs tobe waited for the broadcast message to respond.
#define ODO19_AR2               (16)    // If non-zero then this field contains the lower byte of the non OBD-II standard CAN identifier response.
#define ODO19_PM1               (17)    // If non-zero this indicates that a pre-message needs to be transmitted prior to getting a response to the main message
#define ODO19_LITERS            (18)    // If this field contains a one during a fuel request then this indicated the returned value is in liters and not a percentage of the fuel volume.


#define SUPPORTED                       (0)
#define NOT_LICENCED                    (1)
#define UNSUPPORTED_REQUEST_TYPE        (2)
#define WMI_NOT_SUPPORTED               (3)
#define VIN_NOT_SUPPORTED               (3)
#define MSG_TOO_SHORT                   (4)
#define WRONG_DATA_ENTERED              (5)
#define WRONG_CONVERSION_TYPE           (6)
#define NEGATIVE_RESPONSE_DATA          (7)

#ifdef NORTHAMERICA
#ifdef MAKEADLL
#define EXPORT_DLL __declspec(dllexport)
#else
#define EXPORT_DLL
#endif
EXPORT_DLL Ou32 Odomatic_GetVersion(void);
EXPORT_DLL Ou32 Odomatic_GetRequestByVin(Ou8 *Vin, Ou8 Function, Ou8 *RequestToSend);
EXPORT_DLL Ou32 Odomatic_ConvertByVin(Ou8 *Vin, Ou8 Function, Ou8 *ResponseReceived, Ou8 ConversionStyle, Ou32 *ConvertedResult, Ou32 *ConvertedFractionResult);
EXPORT_DLL Ou32 Odomatic_GetRequestByVinSecure(Ou8 *Vin, Ou8 Function, Ou8 *RequestToSend);
EXPORT_DLL Ou32 Odomatic_GetYearMakeModelByVin(Ou8 *Vin, Ou32* Year, Ou8* Make, Ou8* Model);
EXPORT_DLL Ou32 Odomatic_GetAttributeByVin(Ou8 *Vin, Ou8 AttributeNumber, Ou32* Attribute);
#endif

#ifdef EUROPE
Ou32 Odomatic_GetVersion(void);
Ou32 Odomatic_GetRequestByMMYFE(Ou8 *Vin, Ou8 Make, Ou8 Model, Ou16 Year ,Ou8 Fuel,Ou8 Engine, Ou8 Function, Ou8 *RequestToSend);
Ou32 Odomatic_ConvertByMMYFE(Ou8 *ODOVin, Ou8 ODOVMake, Ou8 ODOVModel, Ou16 ODOVYear, Ou8 ODOVFuel, Ou8 ODOVEngine, Ou8 Function, Ou8 *ResponseReceived, Ou8 ConversionUnits, Ou32 *ConvertedResult, Ou32 *ConvertedFractionResult);
Ou32 Odomatic_GetRequestByMMYFESecure(Ou8 *OVin, Ou8 OMake, Ou8 OModel, Ou16 OYear, Ou8 OFuel, Ou8 OEngine, Ou8 Function, Ou8 *RequestToSend);
#endif


#endif