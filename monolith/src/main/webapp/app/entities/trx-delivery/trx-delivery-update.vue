<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2
          id="monolithApp.trxDelivery.home.createOrEditLabel"
          data-cy="TrxDeliveryCreateUpdateHeading"
          v-text="t$('monolithApp.trxDelivery.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="trxDelivery.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="trxDelivery.id" readonly />
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('monolithApp.trxDelivery.deliveryAddress')"
              for="trx-delivery-deliveryAddress"
            ></label>
            <input
              type="text"
              class="form-control"
              name="deliveryAddress"
              id="trx-delivery-deliveryAddress"
              data-cy="deliveryAddress"
              :class="{ valid: !v$.deliveryAddress.$invalid, invalid: v$.deliveryAddress.$invalid }"
              v-model="v$.deliveryAddress.$model"
              required
            />
            <div v-if="v$.deliveryAddress.$anyDirty && v$.deliveryAddress.$invalid">
              <small class="form-text text-danger" v-for="error of v$.deliveryAddress.$errors" :key="error.$uid">{{
                error.$message
              }}</small>
            </div>
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('monolithApp.trxDelivery.deliveryStatus')"
              for="trx-delivery-deliveryStatus"
            ></label>
            <select
              class="form-control"
              name="deliveryStatus"
              :class="{ valid: !v$.deliveryStatus.$invalid, invalid: v$.deliveryStatus.$invalid }"
              v-model="v$.deliveryStatus.$model"
              id="trx-delivery-deliveryStatus"
              data-cy="deliveryStatus"
              required
            >
              <option
                v-for="deliveryStatus in deliveryStatusValues"
                :key="deliveryStatus"
                v-bind:value="deliveryStatus"
                v-bind:label="t$('monolithApp.DeliveryStatus.' + deliveryStatus)"
              >
                {{ deliveryStatus }}
              </option>
            </select>
            <div v-if="v$.deliveryStatus.$anyDirty && v$.deliveryStatus.$invalid">
              <small class="form-text text-danger" v-for="error of v$.deliveryStatus.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('monolithApp.trxDelivery.assignedDriver')"
              for="trx-delivery-assignedDriver"
            ></label>
            <input
              type="text"
              class="form-control"
              name="assignedDriver"
              id="trx-delivery-assignedDriver"
              data-cy="assignedDriver"
              :class="{ valid: !v$.assignedDriver.$invalid, invalid: v$.assignedDriver.$invalid }"
              v-model="v$.assignedDriver.$model"
            />
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('monolithApp.trxDelivery.estimatedDeliveryTime')"
              for="trx-delivery-estimatedDeliveryTime"
            ></label>
            <div class="d-flex">
              <input
                id="trx-delivery-estimatedDeliveryTime"
                data-cy="estimatedDeliveryTime"
                type="datetime-local"
                class="form-control"
                name="estimatedDeliveryTime"
                :class="{ valid: !v$.estimatedDeliveryTime.$invalid, invalid: v$.estimatedDeliveryTime.$invalid }"
                :value="convertDateTimeFromServer(v$.estimatedDeliveryTime.$model)"
                @change="updateInstantField('estimatedDeliveryTime', $event)"
              />
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.trxDelivery.driver')" for="trx-delivery-driver"></label>
            <select class="form-control" id="trx-delivery-driver" data-cy="driver" name="driver" v-model="trxDelivery.driver">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="trxDelivery.driver && mstDriverOption.id === trxDelivery.driver.id ? trxDelivery.driver : mstDriverOption"
                v-for="mstDriverOption in mstDrivers"
                :key="mstDriverOption.id"
              >
                {{ mstDriverOption.id }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.trxDelivery.trxOrder')" for="trx-delivery-trxOrder"></label>
            <select class="form-control" id="trx-delivery-trxOrder" data-cy="trxOrder" name="trxOrder" v-model="trxDelivery.trxOrder">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="trxDelivery.trxOrder && trxOrderOption.id === trxDelivery.trxOrder.id ? trxDelivery.trxOrder : trxOrderOption"
                v-for="trxOrderOption in trxOrders"
                :key="trxOrderOption.id"
              >
                {{ trxOrderOption.id }}
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
<script lang="ts" src="./trx-delivery-update.component.ts"></script>
