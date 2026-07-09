package com.smartcampus.backend.mapper.parking;

import com.smartcampus.backend.dto.parking.ParkingDTO;
import com.smartcampus.backend.dto.parking.ParkingStatusDTO;
import com.smartcampus.backend.entity.parking.Parking;
import org.springframework.stereotype.Component;

@Component
public class ParkingMapper {

    public ParkingDTO toDTO(Parking parking) {
        if (parking == null) return null;
        return ParkingDTO.builder()
                .id(parking.getId())
                .ident(parking.getIdent())
                .nom(parking.getNom())
                .adresse(parking.getAdresse())
                .exploit(parking.getExploit())
                .infor(parking.getInfor())
                .taType(parking.getTaType())
                .type(parking.getType())
                .latitude(parking.getLatitude())
                .longitude(parking.getLongitude())
                .npTotal(parking.getNpTotal())
                .npGlobal(parking.getNpGlobal())
                .npPmr(parking.getNpPmr())
                .npVle(parking.getNpVle())
                .npVeltot(parking.getNpVeltot())
                .npVelec(parking.getNpVelec())
                .np2rmot(parking.getNp2rmot())
                .npCovoit(parking.getNpCovoit())
                .thQuar(parking.getThQuar())
                .thDemi(parking.getThDemi())
                .thHeur(parking.getThHeur())
                .th2(parking.getTh2())
                .th3(parking.getTh3())
                .th4(parking.getTh4())
                .th10(parking.getTh10())
                .th24(parking.getTh24())
                .thNuit(parking.getThNuit())
                .taTitul(parking.getTaTitul())
                .taNtitul(parking.getTaNtitul())
                .taResmoi(parking.getTaResmoi())
                .taNres7j(parking.getTaNres7j())
                .taMoimot(parking.getTaMoimot())
                .taMoivel(parking.getTaMoivel())
                .taHandi(parking.getTaHandi())
                .anServ(parking.getAnServ())
                .secteur(parking.getSecteur())
                .propr(parking.getPropr())
                .typgest(parking.getTypgest())
                .nbNiv(parking.getNbNiv())
                .gabariStd(parking.getGabariStd())
                .gabariMax(parking.getGabariMax())
                .url(parking.getUrl())
                .build();
    }

    public ParkingStatusDTO toStatusDTO(Parking parking) {
    if (parking == null) return null;
    return ParkingStatusDTO.builder()
            .id(parking.getId())
            .ident(parking.getIdent())
            .nom(parking.getNom())
            .adresse(parking.getAdresse())
            .exploit(parking.getExploit())
            .infor(parking.getInfor())
            .taType(parking.getTaType())
            .type(parking.getType())
            .latitude(parking.getLatitude())
            .longitude(parking.getLongitude())
            // Capacités
            .npTotal(parking.getNpTotal())
            .npGlobal(parking.getNpGlobal())
            .npPmr(parking.getNpPmr())
            .npVle(parking.getNpVle())
            .npVeltot(parking.getNpVeltot())
            .npVelec(parking.getNpVelec())
            .np2rmot(parking.getNp2rmot())
            .npCovoit(parking.getNpCovoit())
            // Tarifs
            .thQuar(parking.getThQuar())
            .thDemi(parking.getThDemi())
            .thHeur(parking.getThHeur())
            .th2(parking.getTh2())
            .th3(parking.getTh3())
            .th4(parking.getTh4())
            .th10(parking.getTh10())
            .th24(parking.getTh24())
            .thNuit(parking.getThNuit())
            .taTitul(parking.getTaTitul())
            .taNtitul(parking.getTaNtitul())
            .taResmoi(parking.getTaResmoi())
            .taNres7j(parking.getTaNres7j())
            .taMoimot(parking.getTaMoimot())
            .taMoivel(parking.getTaMoivel())
            .taHandi(parking.getTaHandi())
            // Infra
            .anServ(parking.getAnServ())
            .secteur(parking.getSecteur())
            .propr(parking.getPropr())
            .typgest(parking.getTypgest())
            .nbNiv(parking.getNbNiv())
            .gabariStd(parking.getGabariStd())
            .gabariMax(parking.getGabariMax())
            .url(parking.getUrl())
            .build();
}
}