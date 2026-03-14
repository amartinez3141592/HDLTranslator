module RTL_X(
	input wire PP,
	input wire FF_L,
	input wire clk,
	input wire reset,
	output reg [3:0] Pista,
	output reg [5:0] Posicion,
	output reg [1:0] Comando
);
	reg [5:0] pos;
	reg [3:0] pst;
	reg Flag;
	reg [5:0] next_pos;
	reg [3:0] next_pst;
	reg next_Flag;
	localparam
		S0 = 4'b1000,
		S1 = 4'b0100,
		S2 = 4'b0010,
		S3 = 4'b0001;
	reg [3:0] next_step;
	reg [3:0] step;
	always @(posedge clk or negedge reset) begin
		if (!(reset)) begin
			pos <= 6'b000000;
			pst <= 4'b0000;
			Flag <= 1'b0;
			step <= S0;
		end else begin
			pos <= next_pos;
			pst <= next_pst;
			Flag <= next_Flag;
			step <= next_step;
		end
	end
	always @(*) begin
		next_step = step;
		Posicion = pos;
		Pista = pst;
		next_Flag = !(Flag);
		next_pos = pos;
		next_pst = pst;
		Comando = 2'b00;
		case(step)
			S0: begin
				next_pst = {1,1,1,1};
				next_pos = {1,1,1,1,1,0};
				if (1) begin
					next_step = S1;
				end
			end
			S1: begin
				Comando = {1,1};
				if (!(PP)) begin
					next_step = S1;
				end else if (PP) begin
					next_step = S2;
				end
			end
			S2: begin
				if (Flag) begin
					next_pos = {pos[4],pos[3],pos[2],pos[1],pos[0],pos[5]};
				end
				Comando = {0,0};
				if (!(PP)) begin
					next_step = S1;
				end else if ((PP&&FF_L)) begin
					next_step = S2;
				end else if ((PP&&!(FF_L))) begin
					next_step = S3;
				end
			end
			S3: begin
				if (Flag) begin
					next_pos = {pos[4],pos[3],pos[2],pos[1],pos[0],pos[5]};
				end
				Comando = {0,1};
				if (FF_L) begin
					next_step = S2;
				end else if (!(FF_L)) begin
					next_step = S3;
				end
			end
		endcase
	end
endmodule