<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2
          id="monolithApp.trxOrderStock.home.createOrEditLabel"
          data-cy="TrxOrderStockCreateUpdateHeading"
          v-text="t$('monolithApp.trxOrderStock.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="trxOrderStock.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="trxOrderStock.id" readonly />
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('monolithApp.trxOrderStock.quantityOrdered')"
              for="trx-order-stock-quantityOrdered"
            ></label>
            <input
              type="number"
              class="form-control"
              name="quantityOrdered"
              id="trx-order-stock-quantityOrdered"
              data-cy="quantityOrdered"
              :class="{ valid: !v$.quantityOrdered.$invalid, invalid: v$.quantityOrdered.$invalid }"
              v-model.number="v$.quantityOrdered.$model"
              required
            />
            <div v-if="v$.quantityOrdered.$anyDirty && v$.quantityOrdered.$invalid">
              <small class="form-text text-danger" v-for="error of v$.quantityOrdered.$errors" :key="error.$uid">{{
                error.$message
              }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.trxOrderStock.orderDate')" for="trx-order-stock-orderDate"></label>
            <div class="d-flex">
              <input
                id="trx-order-stock-orderDate"
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
            <label
              class="form-control-label"
              v-text="t$('monolithApp.trxOrderStock.expectedArrivalDate')"
              for="trx-order-stock-expectedArrivalDate"
            ></label>
            <div class="d-flex">
              <input
                id="trx-order-stock-expectedArrivalDate"
                data-cy="expectedArrivalDate"
                type="datetime-local"
                class="form-control"
                name="expectedArrivalDate"
                :class="{ valid: !v$.expectedArrivalDate.$invalid, invalid: v$.expectedArrivalDate.$invalid }"
                :value="convertDateTimeFromServer(v$.expectedArrivalDate.$model)"
                @change="updateInstantField('expectedArrivalDate', $event)"
              />
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.trxOrderStock.supplier')" for="trx-order-stock-supplier"></label>
            <select class="form-control" id="trx-order-stock-supplier" data-cy="supplier" name="supplier" v-model="trxOrderStock.supplier">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="
                  trxOrderStock.supplier && mstSupplierOption.id === trxOrderStock.supplier.id ? trxOrderStock.supplier : mstSupplierOption
                "
                v-for="mstSupplierOption in mstSuppliers"
                :key="mstSupplierOption.id"
              >
                {{ mstSupplierOption.id }}
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
<script lang="ts" src="./trx-order-stock-update.component.ts"></script>
