package com.ciandt.d1.cocast.castview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Cast View Strategy default implementation 
 * 
 * @author Daniel Viveiros
 */
@Singleton
public class DefaultCastViewStrategy extends CastViewStrategy {
    
    @Inject
    private Logger logger;
    
    private DateComparator dateComparator = new DateComparator();
    private PopularityComparator popularityComparator = new PopularityComparator();

    @Override
    public List<CastViewObject> loadObjects(CastView castView) {
        List<CastViewObject> result = new ArrayList<CastViewObject>();
        List<CastViewObject> allData = super.cloneCastViewObjectList(); 
        
        //Order by
        if ( !StringUtils.isEmpty(castView.getOrderBy()) ) {
            if ("popularity".equals(castView.getOrderBy())) {
                Collections.sort(allData, popularityComparator);
            } else if ("date".equals( castView.getOrderBy())) {
                Collections.sort(allData, dateComparator);
            } else {
                throw new RuntimeException( "Order by not supported: " + castView.getOrderBy() );
            }
        }
        
        Integer maxResults = castView.getMaxResults();
        Integer maxAge = castView.getMaxAgeInHours();
        for ( CastViewObject castViewObject: allData ) {
            
            //at any point, breaks the execution if we have already found enough data
            if (result.size() >= maxResults) {
                return result;
            }
            
            //is there is a category filter, apply it right now
            if (!StringUtils.isEmpty(castView.getCategoryFilter())) {
                if (!StringUtils.isEmpty(castViewObject.getCategoryNames())) { 
                    if (!castViewObject.getCategoryNames().contains(castView.getCategoryFilter())) {
                        continue;
                    }
                }
            }
            
            //max age
            if ( (maxAge != null) && (maxAge > 0) ) {
                if ( castViewObject.getDate() != null ) {
                    Long ageInMilliSeconds = System.currentTimeMillis() - castViewObject.getDate().getTime();
                    if ( ageInMilliSeconds > (maxAge * 60 * 60 * 1000) ) {
                        logger.info("Discarding object " + castViewObject);
                        continue;
                    }
                }
            }
            
            result.add(castViewObject);
        }
        
        return result;
    }

    @Override
    public String getStrategyName() {
        return "default";
    }

}
