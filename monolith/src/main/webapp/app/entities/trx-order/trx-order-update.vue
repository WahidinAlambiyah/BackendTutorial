<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2
          id="monolithApp.trxOrder.home.createOrEditLabel"
          data-cy="TrxOrderCreateUpdateHeading"
          v-text="t$('monolithApp.trxOrder.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="trxOrder.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="trxOrder.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.trxOrder.orderDate')" for="trx-order-orderDate"></label>
            <div class="d-flex">
              <input
                id="trx-order-orderDate"
                data-cy="orderDate"
                type="datetime-local"
                class="form-control"
                name="orderDate"
                :class="{ valid: !v$.orderDate.$invalid, invalid: v$.orderDate.$invalid }"
                required
                :value="convertDateTimeFromServer(v$.orderDate.$model)"
                @change="updateInstantField('orderDate', $event)"
              />
            </div>
            <div v-if="v$.orderDate.$anyDirty && v$.orderDate.$invalid">
              <small class="form-text text-danger" v-for="error of v$.orderDate.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.trxOrder.deliveryDate')" for="trx-order-deliveryDate"></label>
            <div class="d-flex">
              <input
                id="trx-order-deliveryDate"
                data-cy="deliveryDate"
                type="datetime-local"
                class="form-control"
                name="deliveryDate"
                :class="{ valid: !v$.deliveryDate.$invalid, invalid: v$.deliveryDate.$invalid }"
                :value="convertDateTimeFromServer(v$.deliveryDate.$model)"
                @change="updateInstantField('deliveryDate', $event)"
              />
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.trxOrder.orderStatus')" for="trx-order-orderStatus"></label>
            <select
              class="form-control"
              name="orderStatus"
              :class="{ valid: !v$.orderStatus.$invalid, invalid: v$.orderStatus.$invalid }"
              v-model="v$.orderStatus.$model"
              id="trx-order-orderStatus"
              data-cy="orderStatus"
              required
            >
              <option
                v-for="orderStatus in orderStatusValues"
                :key="orderStatus"
                v-bind:value="orderStatus"
                v-bind:label="t$('monolithApp.OrderStatus.' + orderStatus)"
              >
                {{ orderStatus }}
              </option>
            </select>
            <div v-if="v$.orderStatus.$anyDirty && v$.orderStatus.$invalid">
              <small class="form-text text-danger" v-for="error of v$.orderStatus.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.trxOrder.paymentMethod')" for="trx-order-paymentMethod"></label>
            <select
              class="form-control"
              name="paymentMethod"
              :class="{ valid: !v$.paymentMethod.$invalid, invalid: v$.paymentMethod.$invalid }"
              v-model="v$.paymentMethod.$model"
              id="trx-order-paymentMethod"
              data-cy="paymentMethod"
              required
            >
              <option
                v-for="paymentMethod in paymentMethodValues"
                :key="paymentMethod"
                v-bind:value="paymentMethod"
                v-bind:label="t$('monolithApp.PaymentMethod.' + paymentMethod)"
              >
                {{ paymentMethod }}
              </option>
            </select>
            <div v-if="v$.paymentMethod.$anyDirty && v$.paymentMethod.$invalid">
              <small class="form-text text-danger" v-for="error of v$.paymentMethod.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.trxOrder.totalAmount')" for="trx-order-totalAmount"></label>
            <input
              type="number"
              class="form-control"
              name="totalAmount"
              id="trx-order-totalAmount"
              data-cy="totalAmount"
              :class="{ valid: !v$.totalAmount.$invalid, invalid: v$.totalAmount.$invalid }"
              v-model.number="v$.totalAmount.$model"
              required
            />
            <div v-if="v$.totalAmount.$anyDirty && v$.totalAmount.$invalid">
              <small class="form-text text-danger" v-for="error of v$.totalAmount.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.trxOrder.mstCustomer')" for="trx-order-mstCustomer"></label>
            <select class="form-control" id="trx-order-mstCustomer" data-cy="mstCustomer" name="mstCustomer" v-model="trxOrder.mstCustomer">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="
                  trxOrder.mstCustomer && mstCustomerOption.id === trxOrder.mstCustomer.id ? trxOrder.mstCustomer : mstCustomerOption
                "
                v-for="mstCustomerOption in mstCustomers"
                :key="mstCustomerOption.id"
              >
                {{ mstCustomerOption.id }}
              </option>
            </select>
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" v-on:click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.cancel')"></span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="v$.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.save')"></span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./trx-order-update.component.ts"></script>
