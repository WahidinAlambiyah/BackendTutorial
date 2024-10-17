<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2
          id="monolithApp.trxCart.home.createOrEditLabel"
          data-cy="TrxCartCreateUpdateHeading"
          v-text="t$('monolithApp.trxCart.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="trxCart.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="trxCart.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.trxCart.totalPrice')" for="trx-cart-totalPrice"></label>
            <input
              type="number"
              class="form-control"
              name="totalPrice"
              id="trx-cart-totalPrice"
              data-cy="totalPrice"
              :class="{ valid: !v$.totalPrice.$invalid, invalid: v$.totalPrice.$invalid }"
              v-model.number="v$.totalPrice.$model"
              required
            />
            <div v-if="v$.totalPrice.$anyDirty && v$.totalPrice.$invalid">
              <small class="form-text text-danger" v-for="error of v$.totalPrice.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.trxCart.customer')" for="trx-cart-customer"></label>
            <select class="form-control" id="trx-cart-customer" data-cy="customer" name="customer" v-model="trxCart.customer">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="trxCart.customer && mstCustomerOption.id === trxCart.customer.id ? trxCart.customer : mstCustomerOption"
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
<script lang="ts" src="./trx-cart-update.component.ts"></script>
