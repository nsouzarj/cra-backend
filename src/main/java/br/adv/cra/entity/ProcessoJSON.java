package br.adv.cra.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessoJSON {
	private String id;
	private String originAreaId;
	private String responsibleAreaId;
	private String legalDepartmentAreaId;
	private String requesterId;
	private String natureId;
	private String folder;
	private String courtId;
	private String courtName;
	private String actionTypeId;
	private String actionTypeName;
	private String identifierNumber;
	private String oldNumber;
	private String statusId;
	private String countryId;
	private String stateId;
	private String cityId;
	private String individual;
	private String individualName;
	private String company;
	private String companyName;
	private List<OutraParteJSON> otherParty;
	private String name1;
	private String otherPartyName;
	private List<ClienteJSON> customer;       
	private String name2;
	private String customerName;
}