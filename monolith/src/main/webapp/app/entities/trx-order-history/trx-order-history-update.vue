<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2
          id="monolithApp.trxOrderHistory.home.createOrEditLabel"
          data-cy="TrxOrderHistoryCreateUpdateHeading"
          v-text="t$('monolithApp.trxOrderHistory.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="trxOrderHistory.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="trxOrderHistory.id" readonly />
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('monolithApp.trxOrderHistory.previousStatus')"
              for="trx-order-history-previousStatus"
            ></label>
            <select
              class="form-control"
              name="previousStatus"
              :class="{ valid: !v$.previousStatus.$invalid, invalid: v$.previousStatus.$invalid }"
              v-model="v$.previousStatus.$model"
              id="trx-order-history-previousStatus"
              data-cy="previousStatus"
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
            <div v-if="v$.previousStatus.$anyDirty && v$.previousStatus.$invalid">
              <small class="form-text text-danger" v-for="error of v$.previousStatus.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('monolithApp.trxOrderHistory.newStatus')"
              for="trx-order-history-newStatus"
            ></label>
            <select
              class="form-control"
              name="newStatus"
              :class="{ valid: !v$.newStatus.$invalid, invalid: v$.newStatus.$invalid }"
              v-model="v$.newStatus.$model"
              id="trx-order-history-newStatus"
              data-cy="newStatus"
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
            <div v-if="v$.newStatus.$anyDirty && v$.newStatus.$invalid">
              <small class="form-text text-danger" v-for="error of v$.newStatus.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('monolithApp.trxOrderHistory.changeDate')"
              for="trx-order-history-changeDate"
            ></label>
            <div class="d-flex">
              <input
                id="trx-order-history-changeDate"
                data-cy="changeDate"
                type="datetime-local"
                class="form-control"
                name="changeDate"
                :class="{ valid: !v$.changeDate.$invalid, invalid: v$.changeDate.$invalid }"
                required
                :value="convertDateTimeFromServer(v$.changeDate.$model)"
                @change="updateInstantField('changeDate', $event)"
              />
            </div>
            <div v-if="v$.changeDate.$anyDirty && v$.changeDate.$invalid">
              <small class="form-text text-danger" v-for="error of v$.changeDate.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
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
<script lang="ts" src="./trx-order-history-update.component.ts"></script>
