package com.tarento.formservice.utils;

/**
 * 
 * @author Darshan Nagesh
 *
 */

public interface Sql {
	
	public interface MetadataSql { 
        final static String GET_CATEGORIES = "SELECT id, code, name, description, store_code from category" ;
        final static String GET_SUB_CATEGORIES = "SELECT id, code, name, description, category_code from subcategory";
        final static String GET_ITEMS = " SELECT id, code, name, description, price, subcategory_code from item "; 
        final static String GET_SERVICE_API="SELECT id, api_name, tag, xlabel, ylabel, description FROM service_apis" ;
        final static String GET_CHART_FOR_SERVICE_API = " select sapis.api_name, chrt.service_api_id, chrt.id, chrt.name, chrt.code, chrt.type " + 
        		" from service_apis sapis left join chart chrt on sapis.id = chrt.service_api_id " + 
        		" where sapis.api_name IN " ; 
        
        final static String GET_DASHBOARD_CONFIGURATIONS = "select " + 
        		" dash.name as dashboardName, dash.code as dashboardCode, dash.is_active as isActive, dash.description as dashboardDescription, dash.id as id, dash.placement as placement, " + 
        		" viz.id as visualizationId, viz.name as visualizationName, viz.is_active as visualizationIsActive, viz.description as visualizationDescription, " + 
        		" chrt.id as chartId, chrt.name as chartName, chrt.code as chartCode, chrt.description as chartDescription, chrt.type as chartType, chrt.filter as chartFilter, " + 
        		" sapis.api_name as serviceApiName, sapis.tag as tag, sapis.xlabel as xlabel, sapis.ylabel as ylabel, sapis.description as serviceApiDescription," + 
        		" sapis.api_method as serviceApiMethod, header.id as headerId, header.label as headerLabel, header.data as headerData, header.field as headerField " + 
        		" from dashboard dash left join dashboard_visualization dashviz on dash.id = dashviz.dashboard_id " + 
        		" left join visualization viz on dashviz.visualization_id = viz.id " + 
        		" left join visualization_chart vizchart on viz.id = vizchart.visualization_id " + 
        		" left join chart chrt on vizchart.chart_id = chrt.id " + 
        		" left join chart_header chrtheader on chrt.id = chrtheader.chart_id " + 
        		" left join header_information header on chrtheader.header_id = header.id " + 
        		" left join service_apis sapis on chrt.service_api_id = sapis.id where dash.is_active = true"; 
        final static String PLACEMENT_FILTER_FOR_HOME = " AND dash.placement = 'HOME'"; 
        final static String PLACEMENT_FILTER_FOR_DASHBOARD = " AND dash.placement = 'DASHBOARD'"; 
        final static String GET_ALL_VISUALIZATION = "SELECT description, name, id FROM visualization "; 
        final static String ADD_NEW_DASHBOARD = "INSERT into dashboard (name, code, description) values (?, ?, ?)";
        final static String MAP_DASHBOARD_VISUALIZATION = "INSERT into dashboard_visualization values (?,?) "; 
        
        final static String RATING_CONFIGURATION_GET = " select config.id as id, config.value as value, config.name as name, config.subtitle as subtitle, config.end_message as endMessage, config.color_code as colorCode, reasons.id as reasonId, " + 
        		" reasons.reason as reason from store_rating_config config left join store_rating_config_reasons confreasons ON config.id = confreasons.rating_id " + 
        		" left join store_rating_reasons reasons ON confreasons.reason_id = reasons.id WHERE config.org_id = ? " ;
        final static String RATING_CONFIG_VERSION = " SELECT version from store_rating_config_version "; 
        final static String TENANT_RATING_CONFIG = " SELECT tenant_name, entry_message, time_out, org_text, org_logo_url, org_color_code, theme_color, org_id from tenant_rating_config where org_id = ? ";
        final static String VERIFY_ORG_PIN = " select id from organization where org_code = ? and org_pin = ? and is_active IS TRUE " ;
        final static String GET_AGE_GROUPS_FOR_ORG = "select id, age_group from tenant_rating_agegroups ag left join tenant_rating_config_agegroups configag " + 
        		" ON ag.id = configag.age_group_id where configag.org_id = ? "; 
	}

}
