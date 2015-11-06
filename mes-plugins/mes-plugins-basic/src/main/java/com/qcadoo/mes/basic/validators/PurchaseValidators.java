package com.qcadoo.mes.basic.validators;

import com.qcadoo.mes.basic.constants.BasicConstants;
import com.qcadoo.mes.basic.constants.PurchaseFields;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.search.SearchCriteriaBuilder;
import com.qcadoo.model.api.search.SearchRestrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PurchaseValidators {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public boolean validatePurchaseUniqueness(final DataDefinition purchaseDD, final Entity purchase){
        Entity product = purchase.getBelongsToField(PurchaseFields.PRODUCT);
        BigDecimal price = purchase.getDecimalField(PurchaseFields.PRICE);

        SearchCriteriaBuilder searchCriteriaBuilder= dataDefinitionService
                .get(BasicConstants.PLUGIN_IDENTIFIER, BasicConstants.MODEL_PURCHASE).find()
                    .add(SearchRestrictions.belongsTo(PurchaseFields.PRODUCT, product))
                    .add(SearchRestrictions.eq(PurchaseFields.PRICE, price));

        if(purchase.getId() != null){
            searchCriteriaBuilder.add(SearchRestrictions.ne("id", purchase.getId()));//TODO create global constant "id"
        }

        Entity duplicatePurchase = searchCriteriaBuilder.setMaxResults(1).uniqueResult();

        if(duplicatePurchase != null){
            purchase.addGlobalError("basic.validate.global.error.duplicated");
            return false;
        }

        return true;

    }
}
