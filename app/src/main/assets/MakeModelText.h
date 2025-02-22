// include type.h & MakeModelNA.h before

#include "MakeModelNA.h"

const Ou8 Vehicles[NO_OF_MANUFACTURERS][MAX_NO_OF_CARS][MAX_TEXT_LEN] = {
    { "Buick","Lucerne", "LaCrosse","Regal","Regal/TourX","Encore","Verano","Enclave","Cascada","Envision","EncoreGX","Envista","Unknown model","@"},  //14
    { "Cadillac","XTS","Escalade 4x2","Escalade EXT","Escalade ESV","Escalade 4x4","ATS","CTS","STS","SRX","Hearse/Limo","DTS","CT6","XT5","XT6","ELR","XLR","XT4","CT4","CT5","Lyriq","Unknown model","@" },  //23
    { "Dodge_Chrysler","VW Routan","Pacifica","Town&Country","200","300","Viper","Caliber","Dakota","PT_Cruiser","Aspen","Pacifica","Magnum","Dart","Grand Caravan","Crossfire","Nitro","Durango","Charger","Challenger","Journey","RAM 1500/4000","RAM 2500/3500","RAM 4500/5500","Promaster","Cargo Van","Avenger","Voyager","Sebring","Hornet","Unknown model","@"},  //32
    { "Honda/Acura","TSX","TSX Sport Wagon","ILX","CSX","RL","RLX","NSX","RDX","TL","TLX","ZDX","MDX","Accord","Civic","Fit","CR-V","Odyssey","HR-V","Crosstour","Pilot","Passport","Element","Ridgeline","Fit EV","Clarity","Insight","CR-Z","Integra","Prologue","Unknown model","@" },  //32
    { "Toyota","Scion iQ","Sequoia","Tundra","Yaris","Prius C","Scion xD","Scion tC","Venza","Avalon","Avalon Hybrid","Sienna","Mirai","Scion xB","Scion iM","Corolla iM","Matrix","FJ Cruiser","Highlander","Land Cruiser","Camry","Tacoma","Solara","Prius Prime","4Runner","Prius","RAV4","RAV4 Hybrid","Scion iA","Yaris iA","CH-R","Corolla","GR-Supra","Corolla Cross","BZ4X","GR86","Crown","Grand Highlander","RAV4 Prime","Unknown model","@"},  //40
    { "Lexus","ES 300h","ES 350","ES 1","IS 250","IS 350","IS 200t","IS 300","IS 350C","IS 250C","ISF","IS","RX 350","RX 450h","RX 450hL","RX 350L","HS 250h","RX","RC 350","RCF","RC 200t","RC 300","RC","LS 460","LS 600h","LS 500","LS 500h","LS","ES 250","@"},  //30
    { "Lexus","ESG","CT 200h","LFA","UX 200","UX 250h","CT","GS 450h","GS 350","GS 200t","GSF","GS","GS 460","RX 400h","RX","LX 570","GX 470","GX 460","GX/LX","SC 430","LC 500/500h","NX","LX 600","RZ","TX","Unknown model","@"},  //26
    { "Fiat","124 Spider","500X","500","500L","Promaster City","Maserati","Alfa Romeo","Ferrari","Unknown model","@"},  //11
    { "Land Rover","LR4","Discovery Sport","LR2","Range Rover","Discovery","Range Rover Sport","Evoque","Velar","Defender","Unknown model","@"},  //12
    { "Jaguar","XE","XF","F-Pace","F-Type","E-Pace","I-Pace","XJ","XK","Unknown model","@"},  //11
    { "Jeep","OLD_JEEP","Renegade","Gladiator","Wrangler","Cherokee","Liberty","Grand Cherokee","Patriot","Compass","Unknown model","@"},  //13
    { "Porsche","918 Spyder","Cayenne","Macan","Panamera","Cayman","718 Cayman","Boxter","718 Boxter","911","Taycan","Unknown model","@"},  //13
    { "VW","Atlas","Mii","Passat","Eos","Jetta","CC","Touareg","Beetle","Golf_Jetta","Tiguan","Arteon","Golf","ID.4","Vanagon","Taos","Unknown model","@"}, //18
    { "Audi","A4","Q8","A5","S5","Q7","A7","S7","A6","S6","A8","S8","A3","S3","A4 Allroad","S4","Q5","SQ5","Q3","TT","R8","RS 7","RS 5","RS 3","e-tron","RS 6","RS Q8","SQ7","Q4","SQ8","Unknown model","@"},  //32
    { "Volvo","S80","V70/XC70","XC90","C30/C70/S40/V50","S60","V70","XC70","XC60","V50","C70","S40","V40","C30","S90","V60","V90","V90 Cross Country","V60 Cross Country","S60 Cross Country","XC40","C40","EX30","EX40","EX90","Unknown model","@"},  //26
    { "Ford","Bronco","C-Max","Crown Victoria","EcoSport","Edge","Escape","E-Series","Expedition","Explorer","F150","F250-F550","F650-F750","Fiesta","Flex","Focus","Motor Home/Bus","Fusion","Mustang","Ranger","Taurus","Transit T150","Transit T250-T350","Transit Connect","GT","Econoline","F-600","Maverick","Pre 2011","E-Transit","Mustang Mach-E","F150 Lightning","F150 Hybrid","Bronco Sport","Unknown model","@"}, //38
    { "Lincoln","Continental","MKC","Corsair","MKS","MKT","Nautilus","Aviator","MKX","MKZ","Navigator","Town Car","Unknown model","@"},  //14
    { "Mitsubishi","i-MiEV","Outlander PHEV","Mirage","Galant","Raider","Outlander","Mirage G4","RVR FWD","RVR 4WD","Eclipse","Eclipse Spyder","Endeavour FWD","Endeavour 4WD","Outlander Sp FWD","Outlander Sp 4WD","Outlander","Eclipse Cross FWD","Raider 4WD","Outlander AWC","Eclipse Cross AWD","Lancer","Lancer Ralliart","Lancer Evo","Lancer SB RA 2WD","Lancer SB RA AWD","Outlander AWC","Unknown model","@"},  //29
    { "Nissan","Rogue","Maxima","Titan","Sentra","Versa","Frontier","Quest","Versa Note","NV Passenger","NV Cargo","Juke","Rogue Sport","Altima","NV200","Xterra","Kicks","Pathfinder","GT-R","Rogue Select","Armada","370Z","Murano","Cube","Leaf","Murano CrossCabrio","Micra","Ariya","Z","Unknown model","@"},  //31
    { "Infiniti","QX30","EX","QX50","QX60","JX","FX","QX70","G Series","Q50","Q60","Q40","G Coupe","Q70","M Series","QX56","QX80","QX55","Unknown model","@"},  //20
    { "Subaru","Legacy","Outback","Impreza/WRX","Crosstrek","XV-Crosstrek","Impreza","Forester","WRX","Ascent","Tribeca","BRZ","Levorg","Solterra","Unknown model","@"},  //16
    { "Mazda","Mazda3","Mazda5","Tribute","Mazda2","CX-3","CX-7","RX-8","Mazda6","CX-5","MX-5","CX-9","CX-30","CX-50","MX-30","CX-90","CX-70","Unknown model","@"},  //18
    { "Mercedes","GLC-Class","A-Class","GL-Class/M-Class","Metris","R-Class","CLS-Class","GLE-Class","CL-Class","GLK-Class","B-Class","SLC-Class","SLK-Class","Sprinter","SLS","CLA-Class","SL-Class","GLA-Class","S-Class","C-Class","AMG GT","G-Class","E-Class","GLS-Class","M-Class","GLE-Class","GL-Class","GLB-Class","X-Class","Vito","V-Class","Viano","Citan","EQS","EQB","EQE","EQE-SUV","EQS-SUV","Unknown model","@"},  //38
    { "Hyundai","XCENT/GRAND i10","i 10","i20","Accent","Ioniq E","Ioniq Hybrid","Elanta Touring","Elantra GT","Elantra","Sonata","Sonata Hybrid","Sonata Plug-In","Unknown model","Azera","Genesis","Equus","Genesis Coupe","Tucson","Kona","Veracruz","Veloster","Veloster N","Santa-Fe","Santa-Fe Sport","Palisade","Venue","Santa Cruz","Nexo","Ioniq5","Ioniq6","@"},  //31
    { "Kia","Rio5","Rio","Niro","Stonic","Forte","Forte5","Forte Koup","Optima","Rondo/Carens","Soul EV","Soul","Sorento","K-900","Cadenza","Sedona","Sportage","Stinger","Picanto","Telluride","K5","Seltos","Carnival","EV6","EV9","K4","Kia_Unknown","@"},  //27
    { "Genesis","G80","G90","G70","GV70","GV80","GV60","@"},  //8
    { "BMW","1 Series","2 Series","3 Series","3 Series Hybrid","4 Series","5 Series","5 Series Hybrid","6 Series","7 Series","8 Series","Z4","X1","X2","X3","X3M","X4","X4M","X5","X6","X6M","X7","M2","M3","M4","M5","M6","M8","MINI","i3","i8","X5M","i4","iX","XM","i7","i5","Unknown model","@"},  //38
    { "Mini","Clubman","Hardtop","Countryman","Coupe","Paceman","Roadster","Seven","Convertible","Unknown model","@"},  //11
    { "Chevrolet","Malibu","Impala","City Express","HHR","Cobalt","Incomplete Vehicle","Cruze","Silverado","Suburban","Tahoe","Avalanche","Spark","Low-cab","SS","Camaro","Bolt","Express","Optra","Sonic","Trax","Blazer","Equinox","Captiva Sport","Matiz","Caprice","Orlando","Traverse","Volt","Colorado","Trailblazer","Aveo","Uplander","Corvette","Silverado MD","Unknown Vehicle","@"},  //37
    { "GMC","Yukon","Sierra","Canyon","Savana","Terrain","Acadia","Envoy","Hummer EV","@"},  //10
    { "Smart","Fortwo","@"}, //3
    { "Bentley","Continental FS","Continental GT","Continental GTC","Continental SS","Mulsanne","Bentayga","Unknown model","@"},  //9
    { "Lamborghini","Aventador","Huracan","Urus","Gallardo","Unknown model","@"},  //7 Lambo
    { "Polestar","1","2","3","4","Unknown model","@"},  //7 Polestar
    { "Mercury","Mariner","Grand Marquis","Milan","Montego","Monterey","Mountaineer","Sable","Unknown model","@"},  //9 Mercury
    { "AlfaRomeo","4C","GIULIA","STELVIO","TONALE","Unknown model","@"}, //7
    { "Maserati","Quattroporte","GranTurismo","Levante","Ghibli","MC20","Grecale","Unknown model","@"}, //9
    { "Wagoneer","Wagoneer","Grand Wagoneer","Wagoneer L","Grand Wagoneer L","Unknown model","@"}, //5
    { "@"}
};


