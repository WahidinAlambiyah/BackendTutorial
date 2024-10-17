<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2
          id="monolithApp.trxOrderItem.home.createOrEditLabel"
          data-cy="TrxOrderItemCreateUpdateHeading"
          v-text="t$('monolithApp.trxOrderItem.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="trxOrderItem.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="trxOrderItem.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.trxOrderItem.quantity')" for="trx-order-item-quantity"></label>
            <input
              type="number"
              class="form-control"
              name="quantity"
              id="trx-order-item-quantity"
              data-cy="quantity"
              :class="{ valid: !v$.quantity.$invalid, invalid: v$.quantity.$invalid }"
              v-model.number="v$.quantity.$model"
              required
            />
            <div v-if="v$.quantity.$anyDirty && v$.quantity.$invalid">
              <small class="form-text text-danger" v-for="error of v$.quantity.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.trxOrderItem.price')" for="trx-order-item-price"></label>
            <input
              type="number"
              class="form-control"
              name="price"
              id="trx-order-item-price"
              data-cy="price"
              :class="{ valid: !v$.price.$invalid, invalid: v$.price.$invalid }"
              v-model.number="v$.price.$model"
              required
            />
            <div v-if="v$.price.$anyDirty && v$.price.$invalid">
              <small class="form-text text-danger" v-for="error of v$.price.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.trxOrderItem.order')" for="trx-order-item-order"></label>
            <select class="form-control" id="trx-order-item-order" data-cy="order" name="order" v-model="trxOrderItem.order">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="trxOrderItem.order && trxOrderOption.id === trxOrderItem.order.id ? trxOrderItem.order : trxOrderOption"
                v-for="trxOrderOption in trxOrders"
                :key="trxOrderOption.id"
              >
                {{ trxOrderOption.id }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.trxOrderItem.product')" for="trx-order-item-product"></label>
            <select class="form-control" id="trx-order-item-product" data-cy="product" name="product" v-model="trxOrderItem.product">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="
                  trxOrderItem.product && mstProductOption.id === trxOrderItem.product.id ? trxOrderItem.product : mstProductOption
                "
                v-for="mstProductOption in mstProducts"
                :key="mstProductOption.id"
              >
                {{ mstProductOption.id }}
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
<script lang="ts" src="./trx-order-item-update.component.ts"></script>
