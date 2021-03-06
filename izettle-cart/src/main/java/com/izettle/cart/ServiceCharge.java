package com.izettle.cart;

import java.math.BigDecimal;

public interface ServiceCharge<S> {

    /**
     * The quantity, or the number of units that this item represents
     * @return the quantity, cannot be null
     */
    BigDecimal getQuantity();

    /**
     * The percent VAT that is applied to this service charge, can be null for situations where VAT is not applicable
     * @return the vat percentage
     */
    Float getVatPercentage();

    /**
     * The fixed amount that this service charge affects is target with
     * @return the amount
     */
    Long getAmount();

    /**
     * The percentage that this service charge affects is target with. The actual effect will depend on properties on the
     * target, such as it's gross amount
     * @return The percentage
     */
    Double getPercentage();

    /**
     * Utility method that subclasses need to implement. Inverse here, means the concept of negating the service charge, which
     * would normally be done by cloning it's own fields, but negating the sign on the amount. Used for situations
     * such as full returns.
     * @return the inversed ServiceCharge
     */
    S inverse();

}
