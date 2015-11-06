package com.qcadoo.mes.basic.listeners;


import com.qcadoo.mes.basic.constants.BasicConstants;
import com.qcadoo.mes.basic.constants.PurchaseFields;
import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.search.SearchOrders;
import com.qcadoo.model.api.search.SearchProjections;
import com.qcadoo.view.api.ComponentState;
import com.qcadoo.view.api.ViewDefinitionState;
import com.qcadoo.view.api.components.GridComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@Service
public class PurchasesListListeners {

    @Autowired
    private DataDefinitionService dataDefinitionService;


    private static final String L_AVERAGE_PRICE = "averagePrice";

    public void calculateAveragePrice(final ViewDefinitionState view, final ComponentState state, final String[] args){

        Object averagePrice = getAveragePrice();

        state.addMessage("basic.purchasesList.message.averagePrice", ComponentState.MessageType.SUCCESS,
                averagePrice.toString());
    }

    private Object getAveragePrice(){
        if(notExistsPurchase()){
            return BigDecimal.ZERO;
        }

        Entity avgEntity = dataDefinitionService.get(BasicConstants.PLUGIN_IDENTIFIER,BasicConstants.MODEL_PURCHASE)
                .find()
                .setProjection(SearchProjections.list()
                        .add(SearchProjections.alias(SearchProjections.avg(PurchaseFields.PRICE), L_AVERAGE_PRICE))
                ).addOrder(SearchOrders.asc(L_AVERAGE_PRICE))
                .setMaxResults(1).uniqueResult();

        return  avgEntity.getField(L_AVERAGE_PRICE);
    }

    private boolean notExistsPurchase(){
        List<Entity> entities =dataDefinitionService.get(BasicConstants.PLUGIN_IDENTIFIER,BasicConstants.MODEL_PURCHASE)
                .find()
                .setMaxResults(1).list().getEntities();
        return entities.isEmpty();
    }

}
