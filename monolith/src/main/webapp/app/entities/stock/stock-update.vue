<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2
          id="monolithApp.stock.home.createOrEditLabel"
          data-cy="StockCreateUpdateHeading"
          v-text="t$('monolithApp.stock.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="stock.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="stock.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.stock.quantityAvailable')" for="stock-quantityAvailable"></label>
            <input
              type="number"
              class="form-control"
              name="quantityAvailable"
              id="stock-quantityAvailable"
              data-cy="quantityAvailable"
              :class="{ valid: !v$.quantityAvailable.$invalid, invalid: v$.quantityAvailable.$invalid }"
              v-model.number="v$.quantityAvailable.$model"
              required
            />
            <div v-if="v$.quantityAvailable.$anyDirty && v$.quantityAvailable.$invalid">
              <small class="form-text text-danger" v-for="error of v$.quantityAvailable.$errors" :key="error.$uid">{{
                error.$message
              }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.stock.reorderLevel')" for="stock-reorderLevel"></label>
            <input
              type="number"
              class="form-control"
              name="reorderLevel"
              id="stock-reorderLevel"
              data-cy="reorderLevel"
              :class="{ valid: !v$.reorderLevel.$invalid, invalid: v$.reorderLevel.$invalid }"
              v-model.number="v$.reorderLevel.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.stock.expiryDate')" for="stock-expiryDate"></label>
            <div class="d-flex">
              <input
                id="stock-expiryDate"
                data-cy="expiryDate"
                type="datetime-local"
                class="form-control"
                name="expiryDate"
                :class="{ valid: !v$.expiryDate.$invalid, invalid: v$.expiryDate.$invalid }"
                :value="convertDateTimeFromServer(v$.expiryDate.$model)"
                @change="updateInstantField('expiryDate', $event)"
              />
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.stock.product')" for="stock-product"></label>
            <select class="form-control" id="stock-product" data-cy="product" name="product" v-model="stock.product">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="stock.product && mstProductOption.id === stock.product.id ? stock.product : mstProductOption"
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
<script lang="ts" src="./stock-update.component.ts"></script>
